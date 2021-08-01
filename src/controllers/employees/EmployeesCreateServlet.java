package controllers.employees;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.validators.EmployeeValidator;
import utils.DBUtil;
import utils.EncryptUtil;

/**
 * Servlet implementation class EmployeesCreateServlet
 */
@WebServlet("/employees/create")
public class EmployeesCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesCreateServlet() {
        super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /**--doGetではなくdoPostなのはDBを更新するため。トークンを送って、CreateServlet(edit)への直接アクセス拒否*/
        String _token = request.getParameter("_token");
        if (_token != null && _token.equals(request.getSession().getId())) {
            EntityManager em = DBUtil.createEntityManager();
            /**新しいEmployeeクラスのインスタンス*/
            Employee e = new Employee();
            /**getParameter()名前を引数にすると値を受け取る*/
            /**new.jspで呼び出してる_form.jspで入力した値をEmployeeのフィールドに代入*/
            /*テキストから読みこんだ文字列をリスナーでアプリケーションスコープスコープに格納するのが一般的
             * なのでここではアプリケーションスコープから取り出す
             */
            e.setCode(request.getParameter("code"));
            e.setName(request.getParameter("name"));
            e.setPassword(
                    EncryptUtil.getPasswordEncrypt(
                            request.getParameter("password"),
                            (String)this.getServletContext().getAttribute("pepper")
                            )
                    );
            e.setAdmin_flag(Integer.parseInt(request.getParameter("admin_flag")));
            e.setFail_login_count(0);

            String password = request.getParameter("password");

            /*System.currentTimeMillis()現在の時間をミリ秒で返す、それをEmployeeのフィールドに代入*/
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            e.setCreated_at(currentTime);
            e.setUpdated_at(currentTime);
            e.setDelete_flag(0);

            // バリデーション(カラでないか、社員番号の重複がないか)を実行してエラーがあったら新規登録のフォームに戻る
            // 社員番号の重複チェック、名前が入力されてるかチェック、パスワードが入力されているかチェック
            // codeDuplicateCheckFlag と passwordCheckFlagはUpdateの時しか使わないので
            // trueにしてリスト宣言
            List<String> errors = EmployeeValidator.validate(e, true, true, password);
            if (errors.size() > 0) {
                em.close();

                // フォームに初期値を設定（editの場合だけ、new（create）の場合はカラのインスタンス）
                //さらにエラーメッセージを送る
                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("employee", e);
                request.setAttribute("errors", errors);

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/new.jsp");
                rd.forward(request, response);
            } else {
                // データベースに保存
                em.getTransaction().begin();
                em.persist(e);
                em.getTransaction().commit();
                request.getSession().setAttribute("flush", "登録が完了しました。");
                em.close();

                // indexのページにリダイレクト
                response.sendRedirect(request.getContextPath() + "/employees/index");
            }
        }

    }
}