package controllers.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.Likes;
import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsLikesServlet
 */
@WebServlet("/reports/likes")
public class ReportsLikesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsLikesServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        EntityManager em = DBUtil.createEntityManager();

        Employee emp = (Employee) request.getSession().getAttribute("login_employee");

        List<Likes> like_reports = em.createNamedQuery("getMyFaveReports", Likes.class) //いいねテーブルから自分がいいねを押したレコード取得
                                .setParameter("employee_id", emp.getId())
                                .getResultList();

        List<Report> like_reports2 = new ArrayList<Report>(); //いいねを押したreport用の空のリスト

        for (int i = 0; i < like_reports.size(); i++) { //いいねテーブルから取得したリストからレコードを1件ずつ
            Likes l = like_reports.get(i); //取り出して
            Report r = em.createNamedQuery("getOneReport", Report.class) //日報idから日報オブジェクトを取得
                                .setParameter("id", l.getReport_id())
                                .getSingleResult();
            like_reports2.add(r); //リストに入れる
        }

        em.close();

        request.setAttribute("like_reports2", like_reports2);

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/likes.jsp");
        rd.forward(request, response);
    }

}
