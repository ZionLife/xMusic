package com.zionstudio.xmusic;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.id.edit;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_phone)
    LoginEt mEtPhone;
    @BindView(R.id.et_password)
    LoginEt mEtPassword;

    private static final String TAG = "LoginActivity";
    private static int etHeight;
    private static int drawableDimension;
    private static String mPhone = null;
    private static String mPassword = null;
    @BindView(R.id.btn_login)
    Button mBtnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        etHeight = mEtPassword.getHeight();
        drawableDimension = Utils.dp2px(this, 20);

        //给PhoneEditText设置图标
        final Drawable phoneDrawableLeft = getResources().getDrawable(R.mipmap.phone_login, null);
        phoneDrawableLeft.setBounds(0, 0, drawableDimension, drawableDimension);
        final Drawable phoneDrawableRight = getResources().getDrawable(R.mipmap.empty, null);
        phoneDrawableRight.setBounds(0, 0, drawableDimension, drawableDimension);

        mEtPhone.setCompoundDrawablePadding(Utils.dp2px(this, 5));

        Drawable passDrawableLeft = getResources().getDrawable(R.mipmap.password_login, null);
        passDrawableLeft.setBounds(0, 0, drawableDimension, drawableDimension);
        Drawable passDrawableRigth = getResources().getDrawable(R.mipmap.forgotpass, null);
        passDrawableRigth.setBounds(0, 0, 50, 50);
        mEtPassword.setCompoundDrawables(passDrawableLeft, null, passDrawableRigth, null);
        mEtPassword.setCompoundDrawablePadding(Utils.dp2px(this, 5));

        //设置对右边图片的监听
        mEtPhone.setDrawableRightListener(new LoginEt.DrawableListener() {
            @Override
            public void onDrawableClick() {
                mEtPhone.setText("");
            }
        });

        mEtPassword.setDrawableRightListener(new LoginEt.DrawableListener() {
            @Override
            public void onDrawableClick() {
                Utils.makeToast("稍后补充");
                mEtPassword.requestFocus();
            }
        });
        //设置焦点变化监听
        mEtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEtPhone.setCompoundDrawables(phoneDrawableLeft, null, phoneDrawableRight, null);
                } else {
                    mEtPhone.setCompoundDrawables(phoneDrawableLeft, null, null, null);
                }
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhone = mEtPhone.getText().toString();
                mPassword = mEtPassword.getText().toString();
                if(!TextUtils.isEmpty(mPassword) && !TextUtils.isEmpty(mPhone)){
                    checkAccount();
                }else {
                    Utils.makeToast("请输入用户名和密码");
                }
            }
        });

        //弹出键盘
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void checkAccount() {

    }
}
