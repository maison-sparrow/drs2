package controllers.login;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import utils.DBUtil;
import utils.EncryptUtil;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    // ログイン画面を表示
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("_token", request.getSession().getId()); //   トークンをリクエストスコープへ格納
        if (request.getSession().getAttribute("flush") != null) { // セッションスコープのflushがカラでない場合
            request.setAttribute("flush", request.getSession().getAttribute("flush")); // リクエストスコープに移して
            request.getSession().removeAttribute("flush"); // セッションスコープはカラにする
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
        rd.forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    // ログイン処理を実行
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 認証結果を格納する変数
        Boolean check_login = false;
        Boolean locked = false;

        EntityManager em = DBUtil.createEntityManager();
        String code = request.getParameter("code"); // ログイン画面の入力値を変数宣言
        String plain_pass = request.getParameter("password");

        Employee e = null; // カラのインスタンス

        if (code != null && !code.equals("") && plain_pass != null && !plain_pass.equals("")) { // 両方とも入力されている場合

            String password = EncryptUtil.getPasswordEncrypt(
                    plain_pass,
                    (String) this.getServletContext().getAttribute("pepper"));

            // 社員番号とパスワードが正しいかチェックする
            try { // 削除済みでなく、Javaのcodeとカラムcodeが同じで、Javaのpassとカラムpasswordが同じレコードがあればeに入れる
                e = em.createNamedQuery("checkLoginCodeAndPassword", Employee.class)
                        .setParameter("code", code)
                        .setParameter("pass", password)
                        .getSingleResult();
            } catch (NoResultException ex) {
            }

            if (e != null) { // codeとpasswordが一致するレコードがあった場合

                if (e.getLocked_at() != null) { // ロック時間の登録がある場合

                    Date date_now = new Date(); // 現在時刻
                    Calendar calendar_now = Calendar.getInstance();
                    calendar_now.setTime(date_now);

                    Calendar calendar_locked_at = Calendar.getInstance(); // ロックされた時刻
                    calendar_locked_at.setTime(e.getLocked_at());
                    calendar_locked_at.add(Calendar.DAY_OF_MONTH, 1); // 24時間後

                    if (calendar_locked_at != null && calendar_now.compareTo(calendar_locked_at) >= 0) {
                        // ロック解除時間を過ぎている場合
                        e.setFail_login_count(0); // カウントを0にして
                        e.setLocked_at(null); // ロック時間をカラに
                        em.getTransaction().begin();
                        em.getTransaction().commit();
                        check_login = true; // ログイン変数をtrueに

                    } else { // ロック中の場合はログイン不可
                        e = null;
                        locked = true;
                    }

                } else { // ロック時間の登録がなかった場合
                    check_login = true;
                }

            } else { // codeとpassが一致するレコードが無かった場合

                try {
                    // 削除済みでなく、Javaのcodeとカラムcodeが同じレコードがあればeに入れる
                    e = em.createNamedQuery("checkLoginCode", Employee.class)
                            .setParameter("code", code)
                            .getSingleResult();
                } catch (NoResultException ex) {
                }

                if (e != null) { // コードのみ一致のレコードがあった場合

                    switch (e.getFail_login_count()) {
                    case 2:
                        e.setFail_login_count(3); // カウントを3にして
                        e.setLocked_at(new Timestamp(System.currentTimeMillis())); // ロック時間を保存
                        em.getTransaction().begin();
                        em.getTransaction().commit();
                        request.setAttribute("locked", "パスワードを3回間違えた為、アカウントがロックされました。");
                    break;

                    case 1:
                    case 0:
                        int count = e.getFail_login_count();
                        e.setFail_login_count(count + 1); // カウントに1を足す
                        em.getTransaction().begin();
                        em.getTransaction().commit();
                    break;
                    }
                    e = null;
                }

            } // codeとpassが一致するレコードが無かった場合おわり

        } // codeとpass両方入力されている場合おわり

        if (check_login) { // ログインできた場合
            request.getSession().setAttribute("flush", "ログインしました。");
            request.getSession().setAttribute("login_employee", e);
            response.sendRedirect(request.getContextPath() + "/");

        } else { // ログインできなかった場合
            request.setAttribute("_token", request.getSession().getId());
            request.setAttribute("hasError", "社員番号かパスワードが間違っています。");
            if (locked) {
                request.setAttribute("hasError", "アカウントがロックされています。");
            }
            request.setAttribute("code", code);
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
            rd.forward(request, response);
        }

        em.close();
    } //doPostおわり

}
