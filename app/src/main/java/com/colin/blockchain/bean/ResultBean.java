package com.colin.blockchain.bean;

import java.io.Serializable;

/**
 * Created by Colin on 2017/3/31 11:23.
 * 邮箱：cartier_he@163.com
 * 微信：cartier_he
 */

public class ResultBean implements Serializable {
    /**
     * date : 1490927818194
     * ticker : {"buy":"18.53","high":"19.7","last":"18.55","low":"16.31","sell":"18.55","vol":"3412037.233"}
     */

    public String date;
    public TickerEntity ticker;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public TickerEntity getTicker() {
        return ticker;
    }

    public void setTicker(TickerEntity ticker) {
        this.ticker = ticker;
    }

    public static class TickerEntity {
        /**
         * buy : 18.53
         * high : 19.7
         * last : 18.55
         * low : 16.31
         * sell : 18.55
         * vol : 3412037.233
         */

        public String buy;
        public String high;
        public String last;
        public String low;
        public String sell;
        public String vol;

        public String getBuy() {
            return buy;
        }

        public void setBuy(String buy) {
            this.buy = buy;
        }

        public String getHigh() {
            return high;
        }

        public void setHigh(String high) {
            this.high = high;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public String getLow() {
            return low;
        }

        public void setLow(String low) {
            this.low = low;
        }

        public String getSell() {
            return sell;
        }

        public void setSell(String sell) {
            this.sell = sell;
        }

        public String getVol() {
            return vol;
        }

        public void setVol(String vol) {
            this.vol = vol;
        }
    }
}
