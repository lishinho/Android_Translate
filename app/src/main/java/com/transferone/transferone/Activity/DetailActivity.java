package com.transferone.transferone.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.transferone.transferone.Adapter.DetailRecyclerAdapter;
import com.transferone.transferone.R;
import com.transferone.transferone.base.BaseActivity;
import com.transferone.transferone.entity.Comment;
import com.transferone.transferone.entity.DayTranslateCard;
import com.transferone.transferone.entity.MyItemDecoration;
import com.transferone.transferone.entity.Userinfo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 *
 */

public class DetailActivity extends BaseActivity {

    @BindView(R.id.rv_detail)
    RecyclerView mRecyclerView;
    @BindView(R.id.imageview_detail_like)
    ImageView mImageviewDetailLike;
    @BindView(R.id.textview_detail_like)
    TextView mTextviewDetailLike;
    @BindView(R.id.textview_detail_share)
    TextView mTextviewDetailShare;
    @BindView(R.id.linear_detail_share)
    LinearLayout mLinearDetailShare;
    @BindView(R.id.textview_detail_commit)
    TextView mTextviewDetailCommit;
    @BindView(R.id.linear_detail_comment)
    LinearLayout mLinearDetailComment;
    @BindView(R.id.linear_detail_like)
    LinearLayout mLinearDetailLike;
    @BindView(R.id.edit_detail_comment_content)
    EditText mEditDetailCommentContent;
    @BindView(R.id.button_detail_comment_send)
    Button mButtonDetailCommentSend;
    @BindView(R.id.relative_detail_comment)
    RelativeLayout mRelativeDetailComment;
    @BindView(R.id.linear_detail_enroll)
    LinearLayout mLinearDetailEnroll;
    @BindView(R.id.linear_detail_recyclerview)
    LinearLayout mLinearDetailRecyclerview;

    private RecyclerView.LayoutManager mLayoutManager;
    private DetailRecyclerAdapter mAdapter;
    private List<Comment> mComments = new ArrayList<>();
    private DayTranslateCard data = new DayTranslateCard();
    private int curPos;
    private boolean isCollect = false;
    public final static int PRE_COMMENT_STATE = 0;
    public final static int PRE_REPLY_STATE = 1;
    private int pre_state;
    private AVUser currentuser;

    @OnClick({R.id.linear_detail_share, R.id.linear_detail_comment, R.id.linear_detail_like,
            R.id.linear_detail_recyclerview, R.id.button_detail_comment_send, R.id.linear_detail_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.linear_detail_share:
                share();
                break;
            case R.id.linear_detail_back:
                onBackPressed();
                finish();
                break;
            case R.id.linear_detail_comment:
                // 弹出输入法
                InputMethodManager imm = (InputMethodManager) getApplicationContext()
                        .getSystemService(this.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                // 显示评论框
                mLinearDetailEnroll.setVisibility(View.GONE);
                mRelativeDetailComment.setVisibility(View.VISIBLE);
                mEditDetailCommentContent.setHint("请输入评论");
                mEditDetailCommentContent.setFocusable(true);
                mEditDetailCommentContent.requestFocus();
                pre_state = PRE_COMMENT_STATE;
                break;
            case R.id.linear_detail_like:
                like();
                break;
            case R.id.linear_detail_recyclerview:
                break;
            case R.id.button_detail_comment_send:
                if (pre_state == PRE_COMMENT_STATE) sendComment();
                else if (pre_state == PRE_REPLY_STATE) reply();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        currentuser = AVUser.getCurrentUser();
        Intent intent = getIntent();
        data.setParagraph_translateID(intent.getStringExtra("paragraph_translateID"));
        initDatas();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    mLinearDetailEnroll.setVisibility(View.VISIBLE);
                    mRelativeDetailComment.setVisibility(View.GONE);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initDatas();
    }

    @Override
    public void onBackPressed() {
        // 这里处理逻辑代码，大家注意：该方法仅适用于2.0或更新版的sdk
        super.onBackPressed();
        return;
    }

    private void initView() {
        mTextviewDetailLike.setText(String.valueOf(data.getLike()));
        if (data.getIsLike() > 0) {
            Glide.with(DetailActivity.this).load(R.drawable.like_click)
                    .bitmapTransform(new CropCircleTransformation(DetailActivity.this))
                    .into(mImageviewDetailLike);
        } else {
            Glide.with(DetailActivity.this).load(R.drawable.like)
                    .bitmapTransform(new CropCircleTransformation(DetailActivity.this))
                    .into(mImageviewDetailLike);
        }
        mTextviewDetailLike.setText(String.valueOf(data.getLike()));
        mTextviewDetailShare.setText(String.valueOf(data.getShare()));
        initRecyclerView();
    }

    private void initRecyclerView() {
        final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DetailRecyclerAdapter(this, data.getTitle(), data.getUsername(), data.getHeadurl(),
                sdf.format(data.getDate()), data.getOriginal(), data.getTranslate(), mComments, isCollect, data.getUserid());
        mRecyclerView.addItemDecoration(new MyItemDecoration());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setHeaderOnclickListener(new DetailRecyclerAdapter.HeaderOnclickListener() {
            @Override
            public void onViewOriginalClick(View view) {
                Intent intent = new Intent(DetailActivity.this, OriginalActivity.class);
                intent.putExtra("paragraphid", data.getParagraph_ID());
                startActivity(intent);
            }

            @Override
            public void onCollectClick(View view, int flag) {
                currentuser = AVUser.getCurrentUser();
                AVQuery<Userinfo> avQuery = new AVQuery<Userinfo>("Userinfo");
                avQuery.whereEqualTo("userid", currentuser.getObjectId());
                switch (flag) {
                    case 0:
                        avQuery.getFirstInBackground(new GetCallback<Userinfo>() {
                            @Override
                            public void done(Userinfo userinfo, AVException e) {
                                if (e == null) {
                                    List mlist = userinfo.getList("collect");
                                    mlist.remove(data.getParagraph_translateID());
                                    userinfo.put("collect", mlist);
                                    userinfo.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            if (e == null) {
                                                Toast.makeText(DetailActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    case 1:
                        avQuery.getFirstInBackground(new GetCallback<Userinfo>() {
                            @Override
                            public void done(Userinfo userinfo, AVException e) {
                                if (e == null) {
                                    if (!userinfo.getList("collect").contains(data.getParagraph_translateID())) {
                                        userinfo.add("collect", data.getParagraph_translateID());
                                        userinfo.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                if (e == null) {
                                                    Toast.makeText(DetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                }
            }
        });
        mAdapter.setItemOnclickListener(new DetailRecyclerAdapter.ItemOnclickListener() {
            @Override
            public void onLikeClick(View view, int position) {
                //点赞
                //1. 更新UI
                mComments.get(position).likeNum++;
                mAdapter.notifyDataSetChanged();
                //2. 更新后台数据
                String commentID = mComments.get(position).id;
                Log.e("Tag", commentID);
                AVObject object = AVObject.createWithoutData("Comment", commentID);
                object.increment("likeNum");
                object.saveInBackground();
            }

            @Override
            public void onReplyClick(View view, int position) {
                // 弹出输入法
                InputMethodManager imm = (InputMethodManager) getApplicationContext()
                        .getSystemService(DetailActivity.this.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                // 显示评论框
                mLinearDetailEnroll.setVisibility(View.GONE);
                mRelativeDetailComment.setVisibility(View.VISIBLE);
                mEditDetailCommentContent.setHint("回复@" + mComments.get(position).userName + "：");
                mEditDetailCommentContent.setFocusable(true);
                mEditDetailCommentContent.requestFocus();
                pre_state = PRE_REPLY_STATE;
                curPos = position;
            }

            @Override
            public void onViewAllRepliesClick(View view, int position) {
                Intent intent = new Intent(DetailActivity.this, ReplyActivity.class);
                Comment originalComment = mComments.get(position);
                intent.putExtra("commentID", originalComment.id);
                intent.putExtra("avatarUrl", originalComment.avatarUrl);
                intent.putExtra("userName", originalComment.userName);
                intent.putExtra("content", originalComment.content);
                intent.putExtra("pubTime", originalComment.pubTime);
                startActivity(intent);
            }
        });
    }

    private void initDatas() {

        AVQuery<Userinfo> avQuery = new AVQuery<>("Userinfo");
        avQuery.whereEqualTo("userid",currentuser.getObjectId());
        avQuery.getFirstInBackground(new GetCallback<Userinfo>() {
             @Override
             public void done(final Userinfo userinfoCur, AVException e) {
                 if (e == null) {
                     AVQuery<AVObject> queryTranslate = new AVQuery<>("Paragraph_Translate");
                     queryTranslate.include("user");
                     queryTranslate.getInBackground(data.getParagraph_translateID(), new GetCallback<AVObject>() {
                         @Override
                         public void done(final AVObject avObject, AVException e) {
                             if(e==null){
                                 AVUser user = AVUser.getCurrentUser();
                                 AVQuery<AVObject> avQuery = new AVQuery<>("Likes");
                                 avQuery.whereEqualTo("user", AVObject.createWithoutData("_User", user.getObjectId()));
                                 avQuery.include("paragraph");
                                 avQuery.findInBackground(new FindCallback<AVObject>() {
                                     @Override
                                     public void done(List<AVObject> list, AVException e) {
                                         if (e==null){
                                             ArrayList<String> listLike = new ArrayList<String>();
                                             for (AVObject avObject : list) {
                                                 AVObject paragraph = avObject.getAVObject("paragraph");
                                                 listLike.add(paragraph.getObjectId());
                                             }
                                             if(listLike.contains(data.getParagraph_translateID())){
                                                 data.setIsLike(1);
                                             }else {
                                                 data.setIsLike(0);
                                             }
                                             data.setParagraph_ID(avObject.getString("paragraphid"));
                                             data.setOriginal(avObject.getString("paragraph"));
                                             data.setTranslate(avObject.getString("translate"));
                                             data.setDate(avObject.getCreatedAt());
                                             data.setComment(avObject.getNumber("comment").intValue());
                                             data.setLike(avObject.getNumber("like").intValue());
                                             data.setShare(avObject.getNumber("share").intValue());
                                             data.setTitle(avObject.getString("title"));
                                             AVObject userInfo = avObject.getAVObject("user");
                                             data.setUsername(userInfo.getString("username"));
                                             data.setHeadurl(userInfo.getString("userhead"));
                                             data.setUserid(userInfo.getObjectId());
                                             List listCollect = userinfoCur.getList("collect");
                                             if(listCollect.contains(data.getParagraph_translateID())){
                                                 isCollect=true;
                                             }else {
                                                 isCollect = false;
                                             }
                                             mComments.clear();
                                             initView();
                                             //2. 获取原文评论信息
                                             String translionID = data.getParagraph_translateID();
                                             AVQuery<AVObject> query = new AVQuery<>("Comment");
                                             query.whereEqualTo("translationID", translionID);
                                             query.findInBackground(new FindCallback<AVObject>() {
                                                 @Override
                                                 public void done(final List<AVObject> list, AVException e) {
                                                     if (e==null){
                                                         //3. 更新UI
                                                         if (list != null) {
                                                             for (AVObject object : list) {
                                                                 Comment comment = new Comment();
                                                                 comment.id = object.getObjectId();
                                                                 comment.userName = object.getString("userName");
                                                                 comment.avatarUrl = object.getString("avatarUrl");
                                                                 comment.content = object.getString("content");
                                                                 comment.likeNum = object.getInt("likeNum");
                                                                 comment.commentNum = object.getInt("commentNum");
                                                                 SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                                                                 comment.pubTime = sdf.format(object.getUpdatedAt()).toString();
                                                                 mComments.add(comment);
                                                             }
                                                             mTextviewDetailCommit.setText(String.valueOf(list.size()));
                                                             mAdapter.notifyDataSetChanged();
                                                         }
                                                     }else {
                                                         Toast.makeText(DetailActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                                     }
                                                 }
                                             });
                                         }else{
                                             Toast.makeText(DetailActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                         }

                                     }

                                 });

                             }else{
                                 Log.e("TAG",e.getMessage());
                                 Toast.makeText(DetailActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
                 }
             }
         });



    }

    private void like() {
        mLinearDetailLike.setClickable(false);
        if (data.getIsLike() > 0) {   //已点赞

            AVQuery<AVObject> avQuery1 = new AVQuery<>("Likes");  //删除数据库表中对应的点赞数据
            AVQuery<AVObject> avQuery2 = new AVQuery<>("Likes");
            avQuery1.whereEqualTo("user", AVObject.createWithoutData("_User", currentuser.getObjectId()));
            avQuery2.whereEqualTo("paragraph", AVObject.createWithoutData("Paragraph_Translate", data.getParagraph_translateID()));
            AVQuery<AVObject> avQuery = AVQuery.and(Arrays.asList(avQuery1, avQuery2));
            avQuery.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(final AVObject avObject, AVException et) {
                    if (et == null) {
                        avObject.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(AVException e) {
                                if(e==null){
                                    AVObject object = AVObject.createWithoutData("Paragraph_Translate", data.getParagraph_translateID());
                                    object.put("like", data.getLike()-1);
                                    object.saveInBackground(new SaveCallback() {  //保存更改后的点赞数据
                                        @Override
                                        public void done(AVException e) {
                                            if (e == null) {
                                                // 存储成功
                                                data.setIsLike(0);
                                                data.setLike(data.getLike() - 1);
                                                Glide.with(DetailActivity.this).load(R.drawable.like)
                                                        .bitmapTransform(new CropCircleTransformation(DetailActivity.this))
                                                        .into(mImageviewDetailLike);
                                                mTextviewDetailLike.setText(String.valueOf(data.getLike()));
                                                mLinearDetailLike.setClickable(true);
                                            } else {
                                                mLinearDetailLike.setClickable(true);
                                                avObject.saveInBackground();   //取消点赞失败，恢复点赞信息
                                                Toast.makeText(DetailActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else {
                                    mLinearDetailLike.setClickable(true);
                                    Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                                }

                            }
                        });
                    } else {
                        mLinearDetailLike.setClickable(true);
                        Toast.makeText(DetailActivity.this, et.getMessage(), Toast.LENGTH_SHORT);
                    }
                }
            });


        } else { //未点赞
            final AVObject object1 = new AVObject("Likes");
            object1.put("user", AVObject.createWithoutData("_User", currentuser.getObjectId()));
            object1.put("paragraph", AVObject.createWithoutData("Paragraph_Translate", data.getParagraph_translateID()));
            object1.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException et) {
                    if (et==null){
                        AVObject avObject = AVObject.createWithoutData("Paragraph_Translate", data.getParagraph_translateID());
                        avObject.put("like", data.getLike()+1);
                        avObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    // 存储成功
                                    data.setIsLike(1);
                                    data.setLike(data.getLike() + 1); //修改当前item的点赞信息
                                    mLinearDetailLike.setClickable(true);
                                    Glide.with(DetailActivity.this).load(R.drawable.like_click)
                                            .bitmapTransform(new CropCircleTransformation(DetailActivity.this))
                                            .into(mImageviewDetailLike);
                                    mTextviewDetailLike.setText(String.valueOf(data.getLike()));
                                } else {
                                    mLinearDetailLike.setClickable(true);
                                    object1.deleteInBackground(); //点赞失败，恢复点赞信息
                                    Toast.makeText(DetailActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        mLinearDetailLike.setClickable(true);
                        Toast.makeText(DetailActivity.this,et.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void share() {
        Intent intent = new Intent(DetailActivity.this, ShareActivity.class);
        intent.putExtra("translate", data.getTranslate());
        intent.putExtra("id", data.getParagraph_ID());
        intent.putExtra("title", data.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        String time = sdf.format(data.getDate());
        intent.putExtra("time", time);
        intent.putExtra("writer", data.getUsername());
        data.setShare(data.getShare() + 1);
        AVObject avObject = AVObject.createWithoutData("Paragraph_Translate", data.getParagraph_translateID());
        avObject.put("share", data.getShare());
        avObject.saveInBackground();
        startActivity(intent);
    }

    private void sendComment() {
        String str = mEditDetailCommentContent.getText().toString();
        if (str.equals("")) {
            Toast.makeText(DetailActivity.this, "评论不能为空", Toast.LENGTH_SHORT).show();
        } else {
            //0. 获取当前用户
            AVUser user = AVUser.getCurrentUser();
            final String userID = user.getObjectId();
            //1. 获取评论信息
            final String content = str;
            final String translationID = data.getParagraph_translateID();
            AVQuery<AVObject> query = new AVQuery<>("Userinfo");
            query.whereEqualTo("userid", userID);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    if (object != null) {
                        //2. 获取用户信息
                        final String userName = object.getString("username");
                        final String avatarUrl = object.getString("userhead");
                        //3. 将评论信息存入数据库
                        final AVObject avObject = new AVObject("Comment");
                        avObject.put("content", content);
                        avObject.put("userName", userName);
                        avObject.put("avatarUrl", avatarUrl);
                        avObject.put("translationID", translationID);
                        avObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    //4. 刷新UI
                                    Comment comment = new Comment();
                                    comment.id = avObject.getObjectId();//回调成功才能获取ObjectId
                                    comment.userName = userName;
                                    comment.avatarUrl = avatarUrl;
                                    comment.content = content;
                                    comment.pubTime = new SimpleDateFormat("MM-dd HH:mm").format(new Date()).toString();
                                    mComments.add(comment);
                                    mAdapter.notifyDataSetChanged();
                                    int newCommentNum = Integer.valueOf(mTextviewDetailCommit.getText().toString()) + 1;
                                    mTextviewDetailCommit.setText(String.valueOf(newCommentNum));
                                }
                            }
                        });
                        //5. 更新数据库译文表的评论数
                        final AVObject translation = AVObject.createWithoutData("Paragraph_Translate", translationID);
                        translation.increment("comment");
                        translation.saveInBackground();
                    }
                }
            });
            //6. 发送成功后清空并收起输入框和输入法
            mEditDetailCommentContent.setText("");
            mRelativeDetailComment.setVisibility(View.GONE);
            mLinearDetailEnroll.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            //7. 提示评论成功
            Toast.makeText(DetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
        }
    }

    private void reply() {
        //回复评论
        //1. 回复非空判断
        final String str = mEditDetailCommentContent.getText().toString();
        if (str.equals("")) {
            Toast.makeText(DetailActivity.this, "回复不能为空", Toast.LENGTH_SHORT).show();
        } else {
            //2. 获取当前用户
            AVUser user = AVUser.getCurrentUser();
            String userID = user.getObjectId();
            //3. 获取回复信息
            AVQuery<AVObject> query = new AVQuery<>("Userinfo");
            query.whereEqualTo("userid", userID);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    if (object != null) {
                        //4. 将回复信息存入数据库
                        AVObject avObject = new AVObject("Reply");
                        avObject.put("content", str);
                        avObject.put("commentID", mComments.get(curPos).id);
                        avObject.put("userName", object.getString("username"));
                        avObject.put("avatarUrl", object.getString("userhead"));
                        avObject.put("replyTo", mComments.get(curPos).userName);
                        avObject.saveInBackground();
                        //5. 更新UI
                        mComments.get(curPos).commentNum++;
                        mAdapter.notifyDataSetChanged();
                        //6. 更新后台评论数据
                        String commentID = mComments.get(curPos).id;
                        final AVObject comment = AVObject.createWithoutData("Comment", commentID);
                        comment.increment("commentNum");
                        comment.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    //7. 带上原评论信息，跳转到回复评论页面
                                    Intent intent = new Intent(DetailActivity.this, ReplyActivity.class);
                                    Comment originalComment = mComments.get(curPos);
                                    intent.putExtra("commentID", originalComment.id);
                                    intent.putExtra("avatarUrl", originalComment.avatarUrl);
                                    intent.putExtra("userName", originalComment.userName);
                                    intent.putExtra("content", originalComment.content);
                                    intent.putExtra("pubTime", originalComment.pubTime);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });

            //8. 发送成功后清空并收起输入框和输入法
            mEditDetailCommentContent.setText("");
            mRelativeDetailComment.setVisibility(View.GONE);
            mLinearDetailEnroll.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            //9. 提示回复成功
            Toast.makeText(DetailActivity.this, "回复成功", Toast.LENGTH_SHORT).show();
        }
    }

    //判断焦点当前位置
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left
                    && event.getY() > top - 20 && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    //删除List中指定的某一项
    private void removeStr(List list, String str) {
        Iterator<String> sListIterator = list.iterator();
        while (sListIterator.hasNext()) {
            String e = sListIterator.next();
            if (e.equals(str)) {
                sListIterator.remove();
            }
        }
    }
}
