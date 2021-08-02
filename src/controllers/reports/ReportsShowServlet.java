package controllers.reports;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
 * Servlet implementation class ReportsShowServlet
 */
@WebServlet("/reports/show")
public class ReportsShowServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsShowServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();

        Report r = em.find(Report.class, Integer.parseInt(request.getParameter("id")));

        Employee e = (Employee) request.getSession().getAttribute("login_employee");

        //★★★intに直す？
        long likes_count = (long)em.createNamedQuery("getLikesCount", Long.class)//,ラッパークラス.classでクエリの戻り値を指定しているので合わせる
                    .setParameter("report_id", r.getId()) //namedqueryに引数が必要な場合はここに入れる
                    .getSingleResult(); //全部で何件か

        //ログインしている従業員がこの日報にいいねを押したかどうか、falseだといいねを押せる
        boolean liked_more_than_one = false;
        Likes l = new Likes();

        try {
            l = em.createNamedQuery("getOneLikes", Likes.class)
                    .setParameter("report_id", r.getId())
                    .setParameter("employee_id", e.getId())
                    .getSingleResult();
        } catch (NoResultException ex) {

        }

        if (l.getId() != null) {
            liked_more_than_one = true;
        }


        //★employees_who_liked_reportは別のEntityのListなので
        //closeするのはrをnewしてすぐでなく、Listを宣言してから。
        em.close();

        request.setAttribute("likes_count", likes_count);
        request.setAttribute("report", r);
        request.setAttribute("liked_more_than_one", liked_more_than_one);
        request.setAttribute("_token", request.getSession().getId());

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/show.jsp");
        rd.forward(request, response);
    }

}
