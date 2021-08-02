package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Table(name = "Likes_table")
@NamedQueries({
    //ログインしている従業員がいいねを押した日報をSELECT
    //SELECT * FROM Likes_table WHERE Employee_id = 該当の従業員ID;
    @NamedQuery(
            name = "getMyFaveReports",
            query = "SELECT l FROM Likes AS l WHERE l.employee_id = :employee_id"
    ),
    //ログインしている従業員がいいねを押した日報を集計
    //SELECT COUNT(*) FROM Likes_table WHERE employee_id = 該当の従業員ID;
    @NamedQuery(
            name = "getFaveReportsCount",
            query = "SELECT COUNT(l) FROM Likes AS l WHERE l.employee_id = :employee_id"
    ),
    //該当の日報に付いたいいねを集計
    //SELECT COUNT(*) FROM Likes_table WHERE report_id = 該当の日報ID;
    @NamedQuery(
            name = "getLikesCount",
            query = "SELECT COUNT(l) FROM Likes AS l WHERE l.report_id = :report_id"
    ),
   //日報id、従業員idをしていしてLikesを取得
    //SELECT * FROM Likes_table WHERE report_id = 該当の日報ID AND employee_id = 該当の従業員ID;
    @NamedQuery(
            name = "getOneLikes",
            query = "SELECT l FROM Likes AS l WHERE l.report_id = :report_id AND l.employee_id = :employee_id"
    )

})
@Entity
public class Likes {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "report_id", nullable = false)
    private int report_id;

    @Column(name = "employee_id", nullable = false)
    private int employee_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getReport_id() {
        return report_id;
    }

    public void setReport_id(int report_id) {
        this.report_id = report_id;
    }

    public int getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(int employee_id) {
        this.employee_id = employee_id;
    }

}