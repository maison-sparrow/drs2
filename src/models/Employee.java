package models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Table(name = "employees2")
@NamedQueries({
    @NamedQuery(
        name = "getAllEmployees",
        query = "SELECT e FROM Employee AS e ORDER BY e.id DESC"
    ),
    @NamedQuery(
        name = "getEmployeesCount",
        query = "SELECT COUNT(e) FROM Employee AS e"
    ),

    //仮称eテーブルの中からjavaでのcodeがテーブルのcodeと同じレコードを数える。
    //社員番号の重複があるかどうか、バリデーションで1以上ならエラーを出す。
    @NamedQuery(
        name = "checkRegisteredCode",
        query = "SELECT COUNT(e) FROM Employee AS e WHERE e.code = :code"
    ),
    //削除済みでなく、Javaのcodeとカラムcodeが同じで、Javaのpassとカラムpasswordが同じレコードがあるか
    @NamedQuery(
            name = "checkLoginCodeAndPassword",
            query = "SELECT e FROM Employee AS e WHERE e.delete_flag = 0 AND e.code = :code AND e.password = :pass"
    ),
    //削除済みでなく、Javaのcodeとカラムcodeが同じレコードがあるか
    @NamedQuery(
            name = "checkLoginCode",
            query = "SELECT e FROM Employee AS e WHERE e.delete_flag = 0 AND e.code = :code"
        )
})
@Entity
public class Employee {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //uniqueは一意制約、すでに存在している社員番号は登録できないとDBに伝える設定
    //上記でDBでも制約し、Javaでも重複しないようにするのが一般的
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", length = 64, nullable = false)
    private String password;

    @Column(name = "admin_flag", nullable = false)
    private Integer admin_flag;

    @Column(name = "created_at", nullable = false)
    private Timestamp created_at;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updated_at;

    @Column(name = "delete_flag", nullable = false)
    private Integer delete_flag;

    @Column(name = "fail_login_count")
    private Integer fail_login_count;

    @Column(name = "locked_at")
    private Timestamp locked_at;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAdmin_flag() {
        return admin_flag;
    }

    public void setAdmin_flag(Integer admin_flag) {
        this.admin_flag = admin_flag;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }

    public Integer getDelete_flag() {
        return delete_flag;
    }

    public void setDelete_flag(Integer delete_flag) {
        this.delete_flag = delete_flag;
    }

    public Integer getFail_login_count() {
        return fail_login_count;
    }

    public void setFail_login_count(Integer fail_login_count) {
        this.fail_login_count = fail_login_count;
    }

    public Timestamp getLocked_at() {
        return locked_at;
    }

    public void setLocked_at(Timestamp locked_at) {
        this.locked_at = locked_at;
    }
}