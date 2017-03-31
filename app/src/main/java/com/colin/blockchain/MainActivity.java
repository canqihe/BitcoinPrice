package com.colin.blockchain;

import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
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
    @BindView(R.id.sell)
    TextView sell;
    int currentCoin = 2;

    String coinNameTx = "Ehtereum Classic\n以太经典";
    String coinType = "etc_cny";
    int drawable;
    PowerManager.WakeLock wakeLock;

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


        PowerManager powerManager = (PowerManager) this.getSystemService(Service.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Lock");
        //是否需计算锁的数量
        wakeLock.setReferenceCounted(false);

    }

    public void initData() {
        getDataFormServer();

        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCoin == 1) {
                    coinNameTx = "Ethereum Classic\n以太经典";
                    coinType = "etc_cny";
                }
                if (currentCoin == 2) {
                    coinNameTx = "Ethereum\n以太坊";
                    coinType = "eth_cny";
                }
                if (currentCoin == 3) {
                    coinNameTx = "Bitcoin\n比特币";
                    coinType = "btc_cny";
                }
                if (currentCoin == 4) {
                    coinNameTx = "Litcoin\n莱特币";
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
                        } else if (Float.parseFloat(resultBean.getTicker().getLast()) > Float.parseFloat(PreUtils.getString(MainActivity.this, "priceA", "0"))) {
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
                        sell.setText(resultBean.getTicker().getSell());
                        exchange.setText(TimeUtil.FormetSize(Float.parseFloat(resultBean.getTicker().getVol())));

                        //Notification
                        NotifyUtil.buildBigText(104, drawable, coinNameTx, resultBean.getTicker().getLast() + "")
                                .show();

                        PreUtils.setString(MainActivity.this, "priceA", resultBean.getTicker().getLast());
                        handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }
}
