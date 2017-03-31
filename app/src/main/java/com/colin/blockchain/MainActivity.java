package com.colin.blockchain;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.colin.blockchain.bean.ResultBean;
import com.colin.blockchain.utils.PreUtils;
import com.colin.blockchain.utils.TimeUtil;
import com.google.gson.Gson;
import com.hss01248.notifyutil.NotifyUtil;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    //请求更新显示的View
    protected static final int MSG_UPDATE_IMAGE = 1;
    //请求暂停轮播
    protected static final int MSG_KEEP_SILENT = 2;
    //请求恢复轮播
    protected static final int MSG_BREAK_SILENT = 3;
    //轮播间隔时间
    protected static final long MSG_DELAY = 3000;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.high)
    TextView high;
    @BindView(R.id.low)
    TextView low;
    @BindView(R.id.buy)
    TextView buy;
    @BindView(R.id.exchange)
    TextView exchange;
    @BindView(R.id.load_progress)
    ProgressBar mProgressBar;
    @BindView(R.id.activity_main)
    RelativeLayout mRelativeLayout;
    @BindView(R.id.coin_name)
    TextView coinName;
    int currentCoin = 2;

    String coinNameTx = "Ehtereum Classic";
    String coinType = "etc_cny";
    int drawable;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (handler.hasMessages(MSG_UPDATE_IMAGE)) {
                handler.removeMessages(MSG_UPDATE_IMAGE);
            }
            switch (msg.what) {
                case MSG_UPDATE_IMAGE:
                    getDataFormServer();
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_KEEP_SILENT://只要不发送消息就暂停了
                    break;
                case MSG_BREAK_SILENT:
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_query_builder_black_24dp);
        NotifyUtil.init(getApplicationContext());//初始化通知
        initData();

    }

    public void initData() {
        getDataFormServer();

        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCoin == 1) {
                    coinNameTx = "Ethereum Classic";
                    coinType = "etc_cny";
                }
                if (currentCoin == 2) {
                    coinNameTx = "Ethereum";
                    coinType = "eth_cny";
                }
                if (currentCoin == 3) {
                    coinNameTx = "Bitcoin";
                    coinType = "btc_cny";
                }
                if (currentCoin == 4) {
                    coinNameTx = "Litcoin";
                    coinType = "ltc_cny";
                    currentCoin = 0;
                }
                currentCoin++;
                getDataFormServer();
            }
        });
    }

    public void getDataFormServer() {
        mProgressBar.setVisibility(View.VISIBLE);
        coinName.setText(coinNameTx);
        OkHttpUtils.get()
                .url("http://api.chbtc.com/data/v1/ticker?currency=" + coinType)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "网络好像出了点问题", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        mProgressBar.setVisibility(View.GONE);
                        ResultBean resultBean = new Gson().fromJson(response, ResultBean.class);
                        getSupportActionBar().setTitle(TimeUtil.stampToDate(resultBean.getDate()));
                        price.setText(resultBean.getTicker().getLast());
                        if (Float.parseFloat(resultBean.getTicker().getLast()) < Float.parseFloat(PreUtils.getString(MainActivity.this, "priceA", "0"))) {
                            mRelativeLayout.setBackground(getResources().getDrawable(R.drawable.green_bg));
                            drawable = R.drawable.ic_trending_down_black_24dp;
                        } else {
                            mRelativeLayout.setBackground(getResources().getDrawable(R.drawable.red_bg));
                            drawable = R.drawable.ic_trending_up_black_24dp;
                        }
                        if (resultBean.getTicker().getLast().length() > 5)
                            price.setTextSize(99.0f);
                        else
                            price.setTextSize(110.0f);
                        high.setText(resultBean.getTicker().getHigh());
                        low.setText(resultBean.getTicker().getLow());
                        buy.setText(resultBean.getTicker().getBuy());
                        exchange.setText(TimeUtil.FormetSize(Float.parseFloat(resultBean.getTicker().getVol())));

                        //Notification
                        NotifyUtil.buildBigText(104, drawable, coinNameTx, resultBean.getTicker().getLast() + "")
                                .setForgroundService()
                                .show();

                        PreUtils.setString(MainActivity.this, "priceA", resultBean.getTicker().getLast());
                        handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    }
                });
    }

}
