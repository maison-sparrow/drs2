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
 * Servlet implementation class EmployeesUpdateServlet
 */
@WebServlet("/employees/update")
public class EmployeesUpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesUpdateServlet() {
        super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //まずは_tokenのチェック
        //_tokenはEditでセッションスコープにいれた値をedit.jsp(_form.jspでformのPOSTで送っているので
        //取り出しはgetSessionでなくgetParameter
        //getParameterはformで送ったものも取り出せる
        String _token = request.getParameter("_token");
        if (_token != null && _token.equals(request.getSession().getId())) {
            EntityManager em = DBUtil.createEntityManager();

            //idを指定してDBからのレコードをインスタンス化
            //（employee_idは_formでは扱っておらずセッションスコープに入っているのでスコープから取り出し。）
            //(_form.jspではユーザーは社員番号を使用し、idカラムは扱わないでformタグでPOSTしていない。）
            Employee e = em.find(Employee.class, (Integer)(request.getSession().getAttribute("employee_id")));

            //現在の値と異なる社員番号が入力されていたら
            //重複チェックを行う指定をする
            Boolean codeDuplicateCheckFlag = true; //まずtrueと宣言
            if(e.getCode().equals(request.getParameter("code"))){ //Ifでcodeが重複していたら
                codeDuplicateCheckFlag = false; //falseと宣言、するとバリデーションされない
            } else{
                e.setCode(request.getParameter("code")); //それ以外の場合は_formでのcodeを得てEmployeeにセット
            }

            //パスワード欄に入力があったら
            //パスワードの入力値チェックを行う指定をする
            Boolean passwordCheckFlag = true; //まずtrueと宣言
            String password = request.getParameter("password"); //_form(edit)からの値を取り出す
            if (passwordCheckFlag == null || password.equals("")) { //カラの場合
                passwordCheckFlag = false; //falseと宣言、するとバリデーションされない

            } else { //カラで無ければ
                e.setPassword( //パスワードをハッシュ化してpasswordへセット、書き換え
                        EncryptUtil.getPasswordEncrypt(
                                password,
                                (String)this.getServletContext().getAttribute("pepper")
                                )
                        );
            }

            //_form(edit)で入力した他の値をセットする
            e.setName(request.getParameter("name"));
            e.setAdmin_flag(Integer.parseInt(request.getParameter("admin_flag")));
            e.setUpdated_at(new Timestamp(System.currentTimeMillis()));
            e.setDelete_flag(0);







            List<String> errors = EmployeeValidator.validate(e, codeDuplicateCheckFlag, passwordCheckFlag, password);
            //バリデーションしてエラーがあればフォームに初期値を設定してeditへ戻る
            if (errors.size() > 0) {
                em.close();

                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("employee", e);
                request.setAttribute("errors", errors);

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/edit.jsp");
                rd.forward(request, response);
            } else { //エラーがなければ
                // データベースを更新
                em.getTransaction().begin();
                em.getTransaction().commit();
                request.getSession().setAttribute("flush", "更新が完了しました。");
                em.close();

                //セッションスコープ上の不要になったデータを削除
                //employee‗idだけはformタグでPOSTでなくセッションスコープに入れていた為。
                request.getSession().removeAttribute("employee_id");

                response.sendRedirect(request.getContextPath() + "/employees/index"); //indexへリダイレクト
            }
        }
    }
}
