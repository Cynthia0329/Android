package com.cqnu.chenyudan.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cqnu.chenyudan.R;
import com.cqnu.chenyudan.activity.Alarm.AlarmActivity;
import com.cqnu.chenyudan.activity.Contact.AddContactInfo;
import com.cqnu.chenyudan.activity.Contact.ModifyContactInfo;
import com.cqnu.chenyudan.model.ContactInfo;
import com.cqnu.chenyudan.service.MainService;
import com.cqnu.chenyudan.util.BaseActivity;

import net.sourceforge.cardme.engine.VCardEngine;
import net.sourceforge.cardme.io.CompatibilityMode;
import net.sourceforge.cardme.io.VCardWriter;
import net.sourceforge.cardme.vcard.VCard;
import net.sourceforge.cardme.vcard.VCardImpl;
import net.sourceforge.cardme.vcard.arch.VCardVersion;
import net.sourceforge.cardme.vcard.types.FNType;
import net.sourceforge.cardme.vcard.types.NType;
import net.sourceforge.cardme.vcard.types.TelType;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 通讯录主界面
 */
public class MainActivity extends BaseActivity {


    //获取库Phone表字段
    private static final String[] PHONES_PROJECTION = new String[]{
            Phone.DISPLAY_NAME, Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_ID, Phone.CONTACT_ID};

    // 联系人显示名称
    private static final int NAME_INDEX_PHONE = 0;

    //电话号码
    private static final int NUMBER_PHONE = 1;

    /*绑定成功后，从Binder对象中得到service*/
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MainService.MainServiceBinder mainBinder = (MainService.MainServiceBinder) iBinder;
            service = mainBinder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    private ListView lv_contact;        //获取控件      列表
    private EditText et_search;         //获取控件  输入框
    private MyAdapter myAdapter;        //自定义适配器
    private MainService service;           //service
    private List<ContactInfo> mContactInfos;    //返回的联系人列表
    private String mName;   //得到长按选中的联系人名字
    private String dName;   //得到单击选中的联系人名字


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView(); //初始化界面
        initData(); //初始化数据
    }

    /*初始化数据*/
    private void initData() {
        /*发起对Service的绑定*/
        Intent intent = new Intent(this, MainService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        new Thread() {
            @Override
            public void run() {
                // 等待服务连接成功，连接成功后就不为null,会跳出循环执行下面的代码
                while (service == null) {
                    SystemClock.sleep(100);
                }
                //这里是在子线程当中，所以可以做耗时操作
                if (service != null) {
                    mContactInfos = service.queryAll();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //这里是在ui线程不能做耗时操作，只能做和ui相关的事，比如将获取到的数据显示到界面
                            lv_contact.setAdapter(myAdapter);       //给listview控件配置适配器
                            //myAdapter.notifyDataSetChanged(); //这句代码是在你如果改变了list数据以后需要调用的
                        }
                    });
                }
            }
        }.start();
    }

    /*刷新界面上的数据*/
    private void refreshData() {
        mContactInfos = service.queryAll();
        myAdapter.notifyDataSetChanged();
    }

    /*初始化界面*/
    private void initView() {
        setContentView(R.layout.activity_main);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        lv_contact = (ListView) findViewById(R.id.lv_contact);      //获取控件  列表
        et_search = (EditText) findViewById(R.id.et_search);        //获取控件  输入框

        myAdapter = new MyAdapter();    //new一个自定义适配器
    }


    /*创建菜单*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*menu菜单点击事件*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:       //全部删除
                service.deleteAll();
                refreshData();
                Toast.makeText(this,"删除全部联系人成功！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_import:       //从系统或者SD卡中导入
                menuImport();
                break;
            case R.id.menu_export:      //导出到SD卡
                menuExport();
                break;
            case R.id.menu_message:     //点击添加事务提醒
                Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
                MainActivity.this.startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /*设置搜索文本监听*/
    public void search(View view) {
        String search = et_search.getText().toString().trim();
        et_search.setCursorVisible(true);
        if (!TextUtils.isEmpty(search)) {
            ContactInfo queryName = service.query(search);
            if (queryName == null) {
                Toast.makeText(this, "没有查询到该联系人", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, ModifyContactInfo.class);
                intent.putExtra("name", search);  //传入搜索的联系人名字
                startActivityForResult(intent, 0);
            }
        } else {
            Toast.makeText(this, "请输入联系人姓名！", Toast.LENGTH_SHORT).show();
        }

    }

    /*点击按钮添加新的联系人信息*/
    public void fab_add(View view) {
        Intent intent = new Intent(MainActivity.this, AddContactInfo.class);
        startActivityForResult(intent, 0);   //startActivityForResult的主要作用就是它可以回传数据,会回调第一个页面的onActivityResult()方法↓
    }


    /*自定义适配器获取列表视图*/
    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mContactInfos.size();
        }

        @Override
        public View getView(final int position, View converview, ViewGroup parent) {
            View view = View.inflate(MainActivity.this, R.layout.item_main, null);    //用inflate()先将其它xml的layout文件找到
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            //再用view去获取该xml上的控件  这里注意！！
            tv_name.setText(mContactInfos.get(position).getName());
            tv_phone.setText(mContactInfos.get(position).getPhone());
            tv_name.setTextSize(20);
            /**当长按时弹出菜单选项*/
            tv_name.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mName = getItem(position).getName();    //得到长按选中的联系人名字
                    startActionMode(new MyCallback());      //调用ActionMode
                    return true;
                }
            });
            /**当单击时，弹出选项进行拨打电话或者发送短信*/
            tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dName = getItem(position).getName();    //得到单击选中的联系人名字

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);   // 工厂设计模式，得到创建对话框的工厂
                    builder.setTitle("你想要...");
                    String[] items = {"拨打电话", "发送短信"};
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
                                callPhone();
                            }    /*打电话*/ else if (i == 1) {
                                sendMessage();
                            }    /*发短信*/
                        }
                    });
                    builder.show();
                }
            });
            return view;
        }

        @Override
        public ContactInfo getItem(int position) {

            return mContactInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }


    /*长按弹出的菜单栏ActionMode*/
    private class MyCallback implements ActionMode.Callback {
        //在初始创建的时候调用
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater(); //getMenuInflater 方法返回一个 MenuInflater 对象，用来加载 ActionMode 的菜单布局文件
            inflater.inflate(R.menu.action_mode_main, menu);    //指定Toolbar上的视图文件
            return true;
        }

        //在创建之后准备绘制的时候调用
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        //当点击 ActionMode 菜单选项的时候调用
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                //点击修改信息
                case R.id.menu_modify:
                    Intent intent = new Intent(MainActivity.this, ModifyContactInfo.class);
                    intent.putExtra("name", mName);  //传入点击的联系人名字
                    startActivityForResult(intent, 0);
                    break;
                //点击删除信息
                case R.id.menu_delete:
                    service.delete(mName);
                    Toast.makeText(MainActivity.this, "删除联系人" + mName + "成功！", Toast.LENGTH_SHORT).show();
                    refreshData();
                    break;
                default:
                    break;
            }
            mode.finish();
            return true;
        }

        //当退出 ActionMode 的时候调用
        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }


















    /******************************方法*********************************************/
    /*导出联系人到SD卡*/
    public void menuExport() {
        performCodeWithPermission("写入文件到SD卡", new PermissionCallback() {
            @Override
            public void hasPermission() {
                try {
                    FileWriter fw = new FileWriter(new File(Environment.getExternalStorageDirectory(), "test01.vcf"));   //创建一个vcf文件
                    VCardImpl vCard = new VCardImpl();                  //创建一个vCard

                    //VCardWriter用于将VCard对象转换成VCard格式的字符串
                    //VCardWriter构造函数的第一个参数表示要转换成哪个版本的VCard字符串，第二个参数说明转换成通用VCard字符串
                    //注：一个VCardWriter对象一次只能转换一个VCard对象
                    VCardWriter vw = new VCardWriter(VCardVersion.V3_0, CompatibilityMode.RFC2426);

                    // 清空内存当中保存的数据，从数据库当中再次读取数据，防止修改数据以后没有及时更新保存到文件
                    mContactInfos.clear();

                    mContactInfos = service.queryAll();

                    for (int i = 0; i < mContactInfos.size(); i++) {

                        FNType fn = new FNType(mContactInfos.get(i).getName());     //FN：vcard对象的名称
                        vCard.setFN(fn);
                        NType n = new NType();      //FN表示一个vcard对象的名称，N表示这个对象名称的组成部分
                        n.setFamilyName(mContactInfos.get(i).getName());
                        n.setGivenName("");
                        vCard.setN(n);
                        //添加电话
                        vCard.addTel(new TelType(mContactInfos.get(i).getPhone()));

                        vw.setVCard(vCard);
                        String vCardString = vw.buildVCardString();
                        fw.append(vCardString);
                        fw.flush();
                    }
                    fw.close();
                    Toast.makeText(MainActivity.this, "导出成功", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void noPermission() {
                Toast.makeText(MainActivity.this, "没有授权读取", Toast.LENGTH_SHORT).show();
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }


    /*导入联系人*/
    private void menuImport() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);   // 工厂设计模式，得到创建对话框的工厂
        builder.setTitle("请选择导入方式...");
        String[] items = {"从系统导入", "从SD卡导入"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {//从系统导入
                    List<ContactInfo> phoneContacts = getPhoneContacts();
                    for (int j = 0; j < phoneContacts.size(); j++) {
                        service.add(phoneContacts.get(j));
                    }
                    refreshData();
                } else if (i == 1) {//从SD卡导入
                    List<ContactInfo> phoneSDcard = getPhoneSDcard();
                    for (int k = 0; k < phoneSDcard.size(); k++) {
                        service.add(phoneSDcard.get(k));
                    }
                    refreshData();
                }
            }
        });
        builder.show();
    }

    /*获取SD卡中vcf文件里的联系人列表*/    //只简单的实现把刚刚导出到sd卡的文件再导入的功能
    private List<ContactInfo> getPhoneSDcard() {
        final ArrayList<ContactInfo> SdInfos = new ArrayList<>();
        performCodeWithPermission("读取SD卡中的文件", new PermissionCallback() {
            @Override
            public void hasPermission() {
                File vCardFile = new File(Environment.getExternalStorageDirectory(), "test01.vcf");
                //VCardEngine Vcar引擎对象，用于解析vcf文件
                VCardEngine ve = new VCardEngine();
                //VCardEngine的parseMultiple方法是从一个文件中解析所有的名片，一个VCard对象表示一张名片
                try {
                    List<VCard> cardList = ve.parseMultiple(vCardFile);
                    for (int i = 0; i < cardList.size(); i++) {
                        VCard currCard = cardList.get(i);
                        //FNType是VCard中的FN数据
                        FNType fn = currCard.getFN();
                        String name = fn.getFormattedName();
                        List<TelType> telList = currCard.getTels();
                        String telephone = null;
                        for (int j = 0; j < telList.size(); j++) {
                            TelType currTel = telList.get(j);
                            telephone = currTel.getTelephone();
                        }
                        SdInfos.add(new ContactInfo(name, telephone, null, null, null, null, null, null));
                    }
                    System.out.println("sdinfos:" + SdInfos.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void noPermission() {
                Toast.makeText(MainActivity.this, "没有授权读取", Toast.LENGTH_SHORT).show();
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE);

        return SdInfos;
    }


    /*获取系统中的联系人列表*/
    private List<ContactInfo> getPhoneContacts() {
        final ArrayList<ContactInfo> contactInfos = new ArrayList<>();
        performCodeWithPermission("读取系统联系人", new PermissionCallback() {
            @Override
            public void hasPermission() {

                // 通过内容提供者获取安卓系统联系人
                ContentResolver resolver = getContentResolver();

                // 获取手机联系人
                Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
                        PHONES_PROJECTION, null, null, null);

                if (phoneCursor != null) {
                    while (phoneCursor.moveToNext()) {

                        // 得到手机号码
                        String phoneNumber = phoneCursor.getString(NUMBER_PHONE);
                        // 当手机号码为空的或者为空字段 跳过当前循环
                        if (TextUtils.isEmpty(phoneNumber))
                            continue;

                        // 得到联系人名称
                        String contactName = phoneCursor
                                .getString(NAME_INDEX_PHONE);

                        contactInfos.add(new ContactInfo(contactName, phoneNumber, null, null, null, null, null, null));
                    }
                    System.out.println("contactInfos:" + contactInfos);
                    phoneCursor.close();
                }
            }

            @Override
            public void noPermission() {
                Toast.makeText(MainActivity.this,"没有授权获取系统联系人", Toast.LENGTH_SHORT).show();
            }
        }, Manifest.permission.READ_CONTACTS);

        return contactInfos;
    }


    /*打电话*/
    private void callPhone() {
        final String number = service.queryPhone(dName);
        if ("".equals(number)) {
            Toast.makeText(MainActivity.this, "该联系人的电话号码为空！", Toast.LENGTH_SHORT).show();
        } else {

            performCodeWithPermission("拨打电话权限", new PermissionCallback() {
                @Override
                public void hasPermission() {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel://" + number));
                    startActivity(intent);
                }

                @Override
                public void noPermission() {

                }
            }, Manifest.permission.CALL_PHONE);
        }
    }


    /*发短信*/
    private void sendMessage() {
        final String number = service.queryPhone(dName);
        if ("".equals(number)) {
            Toast.makeText(MainActivity.this, "该联系人的电话号码为空！", Toast.LENGTH_SHORT).show();
        } else {
            final EditText editText = new EditText(MainActivity.this);
            AlertDialog.Builder inputDialog = new AlertDialog.Builder(MainActivity.this);
            inputDialog.setTitle("请输入你要对联系人 " + dName + "发送的信息").setView(editText);
            inputDialog.setPositiveButton("发送短信",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String message = editText.getText().toString().trim();    //获得输入框的短信内容
                            if ("".equals(message)) {
                                Toast.makeText(MainActivity.this, "你输入的内容为空！", Toast.LENGTH_SHORT).show();
                            } else {

                                performCodeWithPermission("发送短信权限", new PermissionCallback() {
                                    @Override
                                    public void hasPermission() {
                                        SmsManager smsManager = SmsManager.getDefault();
                                        smsManager.sendTextMessage(number, null, message, null, null);
                                    }

                                    @Override
                                    public void noPermission() {

                                    }
                                }, Manifest.permission.SEND_SMS);
                            }

                        }
                    }).show();
        }
    }


}