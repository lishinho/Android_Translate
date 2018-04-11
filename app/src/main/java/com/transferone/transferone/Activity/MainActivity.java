package com.transferone.transferone.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.transferone.transferone.Adapter.MainRecyclerAdapter;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;
import com.transferone.transferone.entity.DayTranslateCard;
import com.transferone.transferone.entity.Userinfo;
import com.transferone.transferone.utils.AlarmUtils;
import com.transferone.transferone.utils.EndLessOnScrollListener;
import com.transferone.transferone.utils.PopupMenu;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 *
 *
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.listview_main)
    ListView mListviewMain;
    @BindView(R.id.drawer_bar_head)
    ImageView mDrawerBarHead;
    @BindView(R.id.drawer_bar_username)
    TextView mDrawerBarUsername;
    @BindView(R.id.recyclerview_main)
    RecyclerView mRecyclerviewMain;
    @BindView(R.id.drawerlayout)
    DrawerLayout mDrawerlayout;
    @BindView(R.id.toolbar_main)
    Toolbar mToolbarMain;
    @BindView(R.id.swiprefresh_main)
    SwipeRefreshLayout mSwiprefreshMain;
    @BindView(R.id.drawer_bar_score)
    TextView mDrawerBarScore;
    @BindView(R.id.drawer_bar_follow)
    TextView mDrawerBarFollow;
    @BindView(R.id.drawer_bar_collect)
    TextView mDrawerBarCollect;
    @BindView(R.id.linear_main_popup)
    LinearLayout mLinearMainPopup;
    @BindView(R.id.image_main_popup)
    ImageView mImageMainPopup;
    @BindView(R.id.text_mian_popup)
    TextView mTextMianPopup;
    @BindView(R.id.linear_main_follow)
    LinearLayout mLinearMainFollow;
    @BindView(R.id.linear_main_collect)
    LinearLayout mLinearMainCollect;
    @BindView(R.id.floataction_main)
    FloatingActionButton mFloatactionMain;

    private ActionBarDrawerToggle mToggle;
    private ArrayList<String> mList = new ArrayList<String>();
    private ArrayList<DayTranslateCard> datalist = new ArrayList<DayTranslateCard>();
    private ArrayList<String> mUser_Likes = new ArrayList<String>();
    private AVUser currentUser = AVUser.getCurrentUser();
    private String mUserName;
    private int mScore;//积分
    private int mFollow;//关注
    private int mCollect;//收藏
    private String mUserPic;//用户头像
    private PopupMenu popupMenu;
    private boolean isExit = false;
    private boolean isFresh = false; //是否刷新
    private MainRecyclerAdapter recyclerAdapter;
    private String myParagraphID = "";
    private List<String> mFollowlist;
    private String userid;//userinfo的id

    private int show_type;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                    initData();
                    break;
                case 2:
                    initRecycler();
                    break;
                case 5:
                    //加载头像
                    InitView();
                    Glide.with(MainActivity.this).load(mUserPic).
                            bitmapTransform(new CropCircleTransformation(MainActivity.this))
                            .into(mDrawerBarHead);
                    mDrawerBarUsername.setText(mUserName);
                    mDrawerBarScore.setText(String.valueOf(mScore));
                    mDrawerBarFollow.setText(String.valueOf(mFollow));
                    mDrawerBarCollect.setText(String.valueOf(mCollect));
                    break;
            }
        }
    };

    Handler exitHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToday();
        alarm();
        initLikes();

        getUserInfo(); //获取用户信息，并更新到页面

    }

    private void initToday() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SharedPreferences sp = getSharedPreferences("translateone", Context.MODE_PRIVATE);
        if (sp.getString("translate_date", "").equals(sdf.format(new Date().getTime()))) {
            myParagraphID = sp.getString("paragraph_id", "");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initLikes();
        getUserInfo();
    }

    @Override
    public void onBackPressed() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(MainActivity.this, "再点击一次退出", Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            exitHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    int i;
                    DayTranslateCard data = intent.getParcelableExtra("data");
                    int position = intent.getIntExtra("position", -1);
                    datalist.set(position, data);
                    recyclerAdapter.notifyItemChanged(position); //更新指定item
                }
                break;
            default:
                break;
        }
    }

    private void initLikes() {
        mUser_Likes.clear();
        AVUser user = AVUser.getCurrentUser();
        AVQuery<AVObject> avQuery = new AVQuery<>("Likes");
        avQuery.whereEqualTo("user", AVObject.createWithoutData("_User", user.getObjectId()));
        avQuery.include("paragraph");
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    for (AVObject avObject : list) {
                        AVObject paragraph = avObject.getAVObject("paragraph");
                        mUser_Likes.add(paragraph.getObjectId());
                    }
                    Message msg = mHandler.obtainMessage();
                    msg.arg1 = 1;
                    mHandler.sendMessage(msg);
                } else {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });
    }

    private void initData() {
        datalist.clear();
        AVQuery<AVObject> avQuery = new AVQuery<>("Paragraph_Translate");
        avQuery.include("user");
        avQuery.orderByDescending("count");
        avQuery.limit(10);
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    int i = 0;
                    while (i < list.size()) {
                        if (show_type == 1) {

                        } else if (show_type == 2) {
                            if (!list.get(i).getString("paragraphid").equals(myParagraphID)) {
                                i++;
                                continue;
                            }
                        } else if (show_type == 3) {
                            if (!mFollowlist.contains(list.get(i).getAVObject("user").getObjectId())) {
                                i++;
                                continue;
                            }
                        }
                        AVObject avObject = list.get(i);
                        DayTranslateCard data = new DayTranslateCard();
                        data.setTitle(avObject.getString("title"));//文章标题
                        data.setContext(avObject.getString("translate"));//译文
                        data.setTranslate(avObject.getString("translate")); //译文
                        data.setComment(avObject.getNumber("comment").intValue());//评论数
                        data.setLike(avObject.getNumber("like").intValue());//点赞数
                        data.setShare(avObject.getNumber("share").intValue());//分享数
                        data.setDate(avObject.getCreatedAt());  //创建时间
                        data.setUpdate(avObject.getUpdatedAt()); //更新时间
                        data.setCount(avObject.getNumber("count").intValue());//数据对应的计数值
                        data.setParagraph_ID(avObject.getString("paragraphid")); //段落原文objectID
                        data.setParagraph_translateID(avObject.getObjectId());   //当前翻译段落objectID
                        if (mUser_Likes.indexOf(avObject.getObjectId()) >= 0) { //是否被当前用户点赞
                            data.setIsLike(1);
                        } else {
                            data.setIsLike(0);
                        }
                        AVObject user = avObject.getAVObject("user");
                        data.setUserid(user.getObjectId());
                        data.setUsername(user.getString("username"));//用户名
                        data.setHeadurl(user.getString("userhead"));//头像
                        datalist.add(data);
                        i++;
                    }
                    if (isFresh) {
                        recyclerAdapter.notifyDataSetChanged();
                        isFresh = false;
                        mSwiprefreshMain.setRefreshing(false);
                    } else {
                        Message msg = mHandler.obtainMessage();
                        msg.arg1 = 2;
                        mHandler.sendMessage(msg);
                    }
                } else {
                    Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void InitView() {
        //popupmenu
        popupMenu = new PopupMenu(this);
        //Toolbar
        mToggle = new ActionBarDrawerToggle(MainActivity.this,
                mDrawerlayout,
                mToolbarMain,
                R.string.open,
                R.string.close);
        mToggle.syncState();
        mDrawerlayout.addDrawerListener(mToggle);

        //侧滑listview
        mList = new ArrayList<String>();
        mList.add("兴趣频道");
        mList.add("译榜");
        mList.add("评论");
        mList.add("草稿");
        mList.add("设置");
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.drawer_list_item, mList);
        mListviewMain.setAdapter(listAdapter);
        mListviewMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (mList.get(position)) {
                    case "兴趣频道":
                        intent = new Intent(MainActivity.this, InterestActivity.class);
                        startActivity(intent);
                        break;
                    case "译榜":
                        intent = new Intent(MainActivity.this, TranslateRankActivity.class);
                        startActivity(intent);
                        break;
                    case "评论":
                        intent = new Intent(MainActivity.this, CommentActivity.class);
                        intent.putExtra("userid", userid);
                        startActivity(intent);
                        break;
                    case "草稿":
                        intent = new Intent(MainActivity.this, DraftActivity.class);
                        startActivity(intent);
                        break;
                    case "设置":
                        intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
        setDrawerLeftEdgeSize(MainActivity.this, mDrawerlayout, 0.2f);//设置侧滑的范围
    }

    private void initRecycler() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyclerviewMain.setLayoutManager(mLayoutManager);
        mRecyclerviewMain.setHasFixedSize(false);
        recyclerAdapter = new MainRecyclerAdapter(MainActivity.this, datalist);
        mRecyclerviewMain.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(new MainRecyclerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DayTranslateCard data = datalist.get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("paragraph_translateID", data.getParagraph_translateID());
                startActivityForResult(intent, 1);
            }
        });
        recyclerAdapter.setOnLikeClickListener(new MainRecyclerAdapter.OnRecyclerViewLikeClickListener() {
            @Override
            public void onLikeClick(View view, int position) {
                recyclerAdapter.notifyItemChanged(position);
                like(position);
            }
        });
        recyclerAdapter.setOnCommentClickListener(new MainRecyclerAdapter.OnRecyclerViewCommentClickListener() {
            @Override
            public void onCommentClick(View view, int position) {
                DayTranslateCard data = datalist.get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("paragraph_translateID", data.getParagraph_translateID());
                startActivityForResult(intent, 1);
            }
        });
        recyclerAdapter.setOnShareClickListener(new MainRecyclerAdapter.OnRecyclerViewShareClickListener() {
            @Override
            public void onShareClick(View view, int position) {
                share(position);
            }
        });
        mRecyclerviewMain.addOnScrollListener(new EndLessOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                loadMoreData();
            }
        });

        mSwiprefreshMain.setColorSchemeResources(R.color.color_Primary);
        mSwiprefreshMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isFresh = true;
                initData();
            }
        });
        mRecyclerviewMain.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
        });

    }

    private void loadMoreData() {
        AVQuery<AVObject> avQuery = new AVQuery<>("Paragraph_Translate");
        avQuery.include("user");
        avQuery.orderByDescending("count");
        avQuery.limit(10);
        avQuery.whereLessThan("count", datalist.get(datalist.size() - 1).getCount());
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (list.size() == 0) recyclerAdapter.setFooterViewVisility(false);
                if (e == null) {
                    if (list == null) {
                        //没有数据了
                        Toast.makeText(MainActivity.this, "没有数据了", Toast.LENGTH_SHORT).show();
                    } else {
                        int i = 0;
                        while (i < list.size()) {
                            if (show_type == 1) {

                            } else if (show_type == 2) {
                                if (!list.get(i).getString("paragraphid").equals(myParagraphID)) {
                                    i++;
                                    continue;
                                }
                            } else if (show_type == 3) {
                                if (!mFollowlist.contains(list.get(i).getString("user"))) {
                                    i++;
                                    continue;
                                }
                            }
                            AVObject avObject = list.get(i);
                            DayTranslateCard data = new DayTranslateCard();
                            data.setTitle(avObject.getString("title"));//文章标题
                            data.setContext(avObject.getString("translate"));//译文
                            data.setTranslate(avObject.getString("translate")); //译文
                            data.setComment(avObject.getNumber("comment").intValue());//评论数
                            data.setLike(avObject.getNumber("like").intValue());//点赞数
                            data.setShare(avObject.getNumber("share").intValue());//分享数
                            data.setDate(avObject.getCreatedAt());  //创建时间
                            data.setUpdate(avObject.getUpdatedAt()); //更新时间
                            data.setCount(avObject.getNumber("count").intValue());//数据对应的计数值
                            data.setParagraph_ID(avObject.getString("paragraphid")); //段落原文objectID
                            data.setParagraph_translateID(avObject.getObjectId());   //当前翻译段落objectID
                            data.setIsLike(0);
                            AVObject user = avObject.getAVObject("user");
                            data.setUserid(user.getObjectId());
                            data.setUsername(user.getString("username"));//用户名
                            data.setHeadurl(user.getString("userhead"));//头像
                            datalist.add(data);
                            i++;
                        }
                        recyclerAdapter.notifyDataSetChanged();

                    }
                } else {
                    Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void like(final int position) {
        final DayTranslateCard data = datalist.get(position);
        if (data.getIsLike() > 0) {
            final AVQuery<AVObject> avQuery1 = new AVQuery<>("Likes");  //删除数据库表中对应的点赞数据
            final AVQuery<AVObject> avQuery2 = new AVQuery<>("Likes");
            avQuery1.whereEqualTo("user", AVObject.createWithoutData("_User", currentUser.getObjectId()));
            avQuery2.whereEqualTo("paragraph", AVObject.createWithoutData("Paragraph_Translate", data.getParagraph_translateID()));
            AVQuery<AVObject> avQuery = AVQuery.and(Arrays.asList(avQuery1, avQuery2));
            avQuery.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(final AVObject avObject, AVException e) {
                    // object 就是符合条件的第一个 AVObject
                    if (e == null) {
                        avObject.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    AVObject object = AVObject.createWithoutData("Paragraph_Translate", data.getParagraph_translateID());
                                    object.put("like", data.getLike() - 1);
                                    object.saveInBackground(new SaveCallback() {  //保存更改后的点赞数据
                                        @Override
                                        public void done(AVException e) {
                                            if (e == null) {
                                                // 存储成功
                                                data.setIsLike(0);
                                                mUser_Likes.remove(data.getParagraph_translateID());
                                                data.setLike(data.getLike() - 1);//修改当前item的点赞信息
                                                recyclerAdapter.notifyItemChanged(position);
                                            } else {
                                                avObject.saveInBackground();  //信息保存不成功，恢复点赞信息
                                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                    }
                }
            });
        } else {
            final AVObject object1 = new AVObject("Likes");
            object1.put("user", AVObject.createWithoutData("_User", currentUser.getObjectId()));
            object1.put("paragraph", AVObject.createWithoutData("Paragraph_Translate", data.getParagraph_translateID()));
            object1.saveInBackground(new SaveCallback() {   //新建点赞数据
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        AVObject avObject = AVObject.createWithoutData("Paragraph_Translate", data.getParagraph_translateID());
                        avObject.put("like", data.getLike() + 1);
                        avObject.saveInBackground(new SaveCallback() { //修改翻译数据点赞信息
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    // 存储成功
                                    data.setIsLike(1);
                                    data.setLike(data.getLike() + 1); //修改当前item的点赞信息
                                    mUser_Likes.add(data.getParagraph_translateID());
                                    recyclerAdapter.notifyItemChanged(position);
                                } else {
                                    object1.deleteInBackground();   //信息保存不成功，恢复点赞信息
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });  //在点赞表里添加一条数据
        }
    }

    private void share(int position) {
        DayTranslateCard data = datalist.get(position);
        Intent intent = new Intent(MainActivity.this, ShareActivity.class);
        intent.putExtra("translate", data.getTranslate());
        intent.putExtra("id", data.getParagraph_ID());
        intent.putExtra("title", data.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        String time = sdf.format(data.getDate());
        intent.putExtra("time", time);
        intent.putExtra("writer", data.getUsername());
        data.setShare(data.getShare() + 1);
        datalist.set(position, data);
        AVObject avObject = AVObject.createWithoutData("Paragraph_Translate", data.getParagraph_translateID());
        avObject.put("share", data.getShare());
        avObject.saveInBackground();
        recyclerAdapter.notifyItemChanged(position);
        startActivity(intent);
    }

    private void setDrawerLeftEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null) return;
        try {
            // 找到 ViewDragHelper 并设置 Accessible 为true
            Field leftDraggerField =
                    drawerLayout.getClass().getDeclaredField("mLeftDragger");//Right
            leftDraggerField.setAccessible(true);
            ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField.get(drawerLayout);

            // 找到 edgeSizeField 并设置 Accessible 为true
            Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(leftDragger);

            // 设置新的边缘大小
            Point displaySize = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (int) (displaySize.x *
                    displayWidthPercentage)));
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
    }

    public void getUserInfo() { //获取用户信息
        final AVUser avUser = new AVUser().getCurrentUser();
        AVQuery<Userinfo> query = new AVQuery<>("Userinfo");
        query.whereMatches("userid", avUser.getObjectId());
        query.getFirstInBackground(new GetCallback<Userinfo>() {
            @Override
            public void done(Userinfo avObject, AVException e) {
                // object 就是符合条件的第一个 AVObject
                if (e == null) {
                    mUserName = avObject.getUsername();
                    mUserPic = avObject.getUserhead();
                    mScore = avObject.getScore();
                    userid = avObject.getObjectId();
                    if (avObject.getJSONArray("follow") == null) {
                        mFollow = 0;
                    } else {
                        mFollow = avObject.getJSONArray("follow").length();
                    }
                    if (avObject.getList("collect") == null) {
                        mCollect = 0;
                    } else {
                        mCollect = avObject.getJSONArray("collect").length();

                    }
                    Message msg = mHandler.obtainMessage();
                    msg.arg1 = 5;
                    mHandler.sendMessage(msg);
                } else {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void alarm(){
        SharedPreferences sp = getSharedPreferences("translateone", Context.MODE_PRIVATE);
        Boolean alarm = sp.getBoolean("alarm", false);
        if (alarm) {
            AlarmUtils.startRemind(MainActivity.this);
        } else {
            AlarmUtils.stopRemind(MainActivity.this);
        }
    }

    @OnClick({R.id.floataction_main,R.id.drawer_bar_head, R.id.linear_main_popup,
            R.id.linear_main_follow, R.id.linear_main_collect})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.floataction_main:

                intent = new Intent(MainActivity.this, TranslateActivity.class);
                startActivity(intent);//进入翻译页面
                break;
            case R.id.drawer_bar_head:
                intent = new Intent(MainActivity.this, UserInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.linear_main_popup:
                popupMenu.showLocation(R.id.linear_main_popup);// 设置弹出菜单弹出的位置
                // 设置回调监听，获取点击事件
                popupMenu.setOnItemClickListener(new PopupMenu.OnItemClickListener() {
                    @Override
                    public void onClick(PopupMenu.MENUITEM item) {
                        if (item == PopupMenu.MENUITEM.ITEM1) {
                            //全部译文
                            mTextMianPopup.setText("全部");
                            show_type = 1;
                            initData();
                        } else if (item == PopupMenu.MENUITEM.ITEM2) {
                            //我的同段译文
                            show_type = 2;
                            ArrayList<DayTranslateCard> datalist_my = new ArrayList<DayTranslateCard>();
                            mTextMianPopup.setText("我的");
                            if (myParagraphID == "") {
                                Toast.makeText(MainActivity.this, "你尚未完成今日翻译", Toast.LENGTH_SHORT).show();
                            } else {
                                initData();
                            }

                        } else if (item == PopupMenu.MENUITEM.ITEM3) {
                            //关注信息
                            show_type = 3;
                            mTextMianPopup.setText("关注");
                            final ArrayList<DayTranslateCard> datalist_follow = new ArrayList<DayTranslateCard>();
                            AVQuery<Userinfo> avQuery = new AVQuery<Userinfo>("Userinfo");
                            avQuery.whereEqualTo("userid", currentUser.getObjectId());
                            avQuery.getFirstInBackground(new GetCallback<Userinfo>() {
                                @Override
                                public void done(Userinfo userinfo, AVException e) {
                                    if (e == null) {
                                        mFollowlist = new ArrayList<String>();
                                        ;
                                        mFollowlist = userinfo.getList("follow");
                                        for (int i = 0; i < datalist.size(); i++) {
                                            if (mFollowlist.contains(datalist.get(i).getUserid())) {
                                                datalist_follow.add(datalist.get(i));
                                            }
                                        }
                                        initData();
                                    } else {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
                break;
            case R.id.linear_main_follow:
                intent = new Intent(MainActivity.this, FollowActivity.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
                break;
            case R.id.linear_main_collect:
                intent = new Intent(MainActivity.this, CollectActivity.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
