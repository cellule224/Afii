package house.thelittlemountaindev.afii.models;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Charlie One on 10/1/2017.
 */

public class Account {
    private String u_id;
    private String u_auth_id;
    private String u_first_name;
    private String u_last_name;
    private double u_balance;
    private String u_phone_number;
    private int u_pin;
    private String u_profile_pic_url;
    private Long u_info_updated;
    private boolean u_premium;
    private Long u_created;

    public Account(String u_id, String u_auth_id, String u_first_name, String u_last_name,
                   double u_balance, String u_phone_number, int u_pin, String u_profile_pic_url,
                   Long u_info_updated, boolean u_premium, Long u_created) {
        this.u_id = u_id;
        this.u_auth_id = u_auth_id;
        this.u_first_name = u_first_name;
        this.u_last_name = u_last_name;
        this.u_balance = u_balance;
        this.u_phone_number = u_phone_number;
        this.u_pin = u_pin;
        this.u_profile_pic_url = u_profile_pic_url;
        this.u_info_updated = u_info_updated;
        this.u_premium = u_premium;
        this.u_created = u_created;
    }

    public Account() {

    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getU_auth_id() {
        return u_auth_id;
    }

    public void setU_auth_id(String u_auth_id) {
        this.u_auth_id = u_auth_id;
    }

    public String getU_first_name() {
        return u_first_name;
    }

    public void setU_first_name(String u_first_name) {
        this.u_first_name = u_first_name;
    }

    public String getU_last_name() {
        return u_last_name;
    }

    public void setU_last_name(String u_last_name) {
        this.u_last_name = u_last_name;
    }

    public double getU_balance() {
        return u_balance;
    }

    public void setU_balance(double u_balance) {
        this.u_balance = u_balance;
    }

    public String getU_phone_number() {
        return u_phone_number;
    }

    public void setU_phone_number(String u_phone_number) {
        this.u_phone_number = u_phone_number;
    }

    public int getU_pin() {
        return u_pin;
    }

    public void setU_pin(int u_pin) {
        this.u_pin = u_pin;
    }

    public String getU_profile_pic_url() {
        return u_profile_pic_url;
    }

    public void setU_profile_pic_url(String u_profile_pic_url) {
        this.u_profile_pic_url = u_profile_pic_url;
    }

    public Long getU_info_updated() {
        return u_info_updated;
    }

    public void setU_info_updated(Long u_info_updated) {
        this.u_info_updated = u_info_updated;
    }

    public boolean isU_premium() {
        return u_premium;
    }

    public void setU_premium(boolean u_premium) {
        this.u_premium = u_premium;
    }

    public Long getU_created() {
        return u_created;
    }

    public void setU_created(Long u_created) {
        this.u_created = u_created;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("u_id",u_id);
        result.put("u_auth_id",u_auth_id);
        result.put("u_first_name",u_first_name);
        result.put("u_last_name",u_last_name);
        result.put("u_balance",u_balance);
        result.put("u_phone_number",u_phone_number);
        result.put("u_pin",u_pin);
        result.put("u_profile_pic_url",u_profile_pic_url);
        result.put("u_info_updated",u_info_updated);
        result.put("u_premium",u_premium);
        result.put("u_created",u_created);

        return result;
    }
}
