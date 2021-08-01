package models.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import models.Employee;
import utils.DBUtil;

public class EmployeeValidator {
    public static List<String> validate(Employee e, Boolean codeDuplicateCheckFlag, Boolean passwordCheckFlag, String password) {



        List<String> errors = new ArrayList<String>();

        String code_error = validateCode(e.getCode(), codeDuplicateCheckFlag);
        if(!code_error.equals("")) {
            errors.add(code_error);
        }

        String name_error = validateName(e.getName());
        if(!name_error.equals("")) {
            errors.add(name_error);
        }

        String password_error = validatePassword(passwordCheckFlag, password);
        if(!password_error.equals("")) {
            errors.add(password_error);
        }

        return errors;
    }

    // 社員番号
    private static String validateCode(String code, Boolean codeDuplicateCheckFlag) {
        // 必須入力チェック
        if(code == null || code.equals("")) {
            return "社員番号を入力してください。";
        }

        // すでに登録されている社員番号との重複チェック
        // 削除済みを含む社員数が多い場合を考えてlong型へのキャストだが、多くなければintでもOKのはず。
        if(codeDuplicateCheckFlag) {
            EntityManager em = DBUtil.createEntityManager();
            long employees_count = (long)em.createNamedQuery("checkRegisteredCode", Long.class).setParameter("code", code).getSingleResult();
            em.close();
            if(employees_count > 0) {
                return "入力された社員番号の情報はすでに存在しています。";
            }
        }

        return "";
    }

    // 社員名の必須入力チェック
    private static String validateName(String name) {
        if(name == null || name.equals("")) {
            return "氏名を入力してください。";
        }

        return "";
    }

    //パスワード
    private static String validatePassword(Boolean passwordCheckFlag, String password) {
        // 必須入力チェック
        if(passwordCheckFlag && (password == null || password.equals(""))) {
            return "パスワードを入力してください。";
        }

        // 文字列チェック

        Pattern p1 = Pattern.compile("(?=.*[A-Z])");
        Matcher m1 = p1.matcher(password);
        boolean passwordcheck1 = m1.find();

        Pattern p2 = Pattern.compile("(?=.*[a-z])");
        Matcher m2 = p2.matcher(password);
        boolean passwordcheck2 = m2.find();

        Pattern p3 = Pattern.compile("(?=.*[0-9])");
        Matcher m3 = p3.matcher(password);
        boolean passwordcheck3 = m3.find();

        Pattern p4 = Pattern.compile("(?=.*[@_-])");
        Matcher m4 = p4.matcher(password);
        boolean passwordcheck4 = m4.find();

        List<String> password_check_errors = new ArrayList<String>();

        if(!passwordcheck1) {
            password_check_errors.add("英大文字を使用");
        }
        if(!passwordcheck2) {
            password_check_errors.add("英小文字を使用");
        }
        if(!passwordcheck3) {
            password_check_errors.add("数字を使用");
        }
        if(!passwordcheck4) {
            password_check_errors.add("記号（@-_）を使用");
        }
        if(password.length() < 8 || password.length() > 50) {
            password_check_errors.add("文字数は8～50文字に");
        }

        if(passwordCheckFlag && password_check_errors != null) {
            String password_check_errors_join = String.join("、", password_check_errors);
            String send_password_check_errors = "パスワードが条件を満たしていません。" + password_check_errors_join + "してください。";

              return send_password_check_errors;
            }
        return "";
    }

}