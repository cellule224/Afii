package house.thelittlemountaindev.afii.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Transaction {
    private String tr_id;
    private String tr_sender_id;
    private String tr_sender_prenom;
    private String tr_sender_phone_num;
    private String tr_receiver_id;
    private String tr_receiver_prenom;
    private String tr_receiver_phone_num;
    private String tr_amount;
    private String tr_note;
    private String tr_delayed;
    private Long tr_time;
    private String tr_cancelled;

    public Transaction() {
    }

    public Transaction(String tr_id, String tr_sender_id, String tr_sender_prenom, String tr_sender_phone_num, String tr_receiver_id, String tr_receiver_prenom, String tr_receiver_phone_num, String tr_amount, String tr_note, String tr_delayed, Long tr_time, String tr_cancelled) {
        this.tr_id = tr_id;
        this.tr_sender_id = tr_sender_id;
        this.tr_sender_prenom = tr_sender_prenom;
        this.tr_sender_phone_num = tr_sender_phone_num;
        this.tr_receiver_id = tr_receiver_id;
        this.tr_receiver_prenom = tr_receiver_prenom;
        this.tr_receiver_phone_num = tr_receiver_phone_num;
        this.tr_amount = tr_amount;
        this.tr_note = tr_note;
        this.tr_delayed = tr_delayed;
        this.tr_time = tr_time;
        this.tr_cancelled = tr_cancelled;
    }

    public String getTr_id() {
        return tr_id;
    }

    public void setTr_id(String tr_id) {
        this.tr_id = tr_id;
    }

    public String getTr_sender_id() {
        return tr_sender_id;
    }

    public void setTr_sender_id(String tr_sender_id) {
        this.tr_sender_id = tr_sender_id;
    }

    public String getTr_sender_prenom() {
        return tr_sender_prenom;
    }

    public void setTr_sender_prenom(String tr_sender_prenom) {
        this.tr_sender_prenom = tr_sender_prenom;
    }

    public String getTr_sender_phone_num() {
        return tr_sender_phone_num;
    }

    public void setTr_sender_phone_num(String tr_sender_phone_num) {
        this.tr_sender_phone_num = tr_sender_phone_num;
    }

    public String getTr_receiver_id() {
        return tr_receiver_id;
    }

    public void setTr_receiver_id(String tr_receiver_id) {
        this.tr_receiver_id = tr_receiver_id;
    }

    public String getTr_receiver_prenom() {
        return tr_receiver_prenom;
    }

    public void setTr_receiver_prenom(String tr_receiver_prenom) {
        this.tr_receiver_prenom = tr_receiver_prenom;
    }

    public String getTr_receiver_phone_num() {
        return tr_receiver_phone_num;
    }

    public void setTr_receiver_phone_num(String tr_receiver_phone_num) {
        this.tr_receiver_phone_num = tr_receiver_phone_num;
    }

    public CharSequence getTr_amount() {
        return tr_amount;
    }

    public void setTr_amount(String tr_amount) {
        this.tr_amount = tr_amount;
    }

    public String getTr_note() {
        return tr_note;
    }

    public void setTr_note(String tr_note) {
        this.tr_note = tr_note;
    }

    public String getTr_delayed() {
        return tr_delayed;
    }

    public void setTr_delayed(String tr_delayed) {
        this.tr_delayed = tr_delayed;
    }

    public Long getTr_time() {
        return tr_time;
    }

    public void setTr_time(Long tr_time) {
        this.tr_time = tr_time;
    }

    public String getTr_cancelled() {
        return tr_cancelled;
    }

    public void setTr_cancelled(String tr_cancelled) {
        this.tr_cancelled = tr_cancelled;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("tr_id",tr_id);
        result.put("tr_sender_id",tr_sender_id);
        result.put("tr_sender_prenom",tr_sender_prenom);
        result.put("tr_sender_phone_num",tr_sender_phone_num);
        result.put("tr_receiver_id",tr_receiver_id);
        result.put("tr_receiver_prenom",tr_receiver_prenom);
        result.put("tr_receiver_phone_num",tr_receiver_phone_num);
        result.put("tr_amount", tr_amount);
        result.put("tr_note",tr_note);
        result.put("tr_delayed",tr_delayed);
        result.put("tr_cancelled", tr_cancelled);

        return result;
    }

}
