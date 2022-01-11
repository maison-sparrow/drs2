package controllers.reports;

import java.io.IOException;

import javax.persistence.EntityManager;
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
 * Servlet implementation class ReportsLikeServlet
 */
@WebServlet("/reports/push_likes")
public class ReportsPushLikesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsPushLikesServlet() {
        super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();
        Report r = em.find(Report.class, Integer.parseInt(request.getParameter("report_id")));
        Employee e = (Employee) request.getSession().getAttribute("login_employee");

        //List<Employee> employees_who_liked_report = new ArrayList<Employee>();
        //employees_who_liked_report = r.getEmployees_who_liked_report();

        String status = request.getParameter("status");

        Likes l = new Likes();

        if (status.equals("push_like")) {
            l.setEmployee_id(e.getId());
            l.setReport(r);

            em.getTransaction().begin();
            em.persist(l);
        }

        if (status.equals("push_cancel")) {

            l = em.createNamedQuery("getOneLikes", Likes.class)//,ラッパークラス.classでクエリの戻り値を指定しているので合わせる
                    .setParameter("report", r)
                    .setParameter("employee_id", e.getId())
                    .getSingleResult();
            em.getTransaction().begin();
            em.remove(l);
        }



        em.getTransaction().commit();
        em.close();

        response.sendRedirect(request.getContextPath() + "/reports/show?id=" + r.getId());

    }
}
