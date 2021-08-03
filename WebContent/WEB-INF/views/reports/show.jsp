<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:import url="/WEB-INF/views/layout/app.jsp">
    <c:param name="content">
        <c:choose>
            <c:when test="${report != null}">
                <h2>日報 詳細ページ</h2>

                <c:choose>
                    <c:when test="${!liked_more_than_one}">

                        <table class="likes">
                            <tbody>
                                <tr>
                                    <th><img src="<c:url value='/like_white.png' />" alt="いいね"
                                        width="19" height="23"></th>
                                    <td><c:out value="${likes_count}" /></td>
                                </tr>
                            </tbody>
                        </table>
                        <form method="post" action="<c:url value='/reports/push_likes' />">
                            <input type="hidden" name="status" value="push_like" /> <input
                                type="hidden" name="report_id" value="${report.id}" />
                            <button class="like_button" type="submit">いいね</button>
                        </form>
                    </c:when>
                    <c:when test="${liked_more_than_one}">
                        <table class="likes">
                            <tbody>
                                <tr>
                                    <th><img src="<c:url value='/like_gray.png' />" alt="いいね"
                                        width="19" height="23"></th>
                                    <td><c:out value="${likes_count}" /></td>
                                </tr>
                            </tbody>
                        </table>
                        <form method="post" action="<c:url value='/reports/push_likes' />">
                            <input type="hidden" name="status" value="push_cancel" /> <input
                                type="hidden" name="report_id" value="${report.id}" />
                            <button class="like_button" type="submit">いいね取消</button>
                        </form>
                    </c:when>
                </c:choose>

                <table>
                    <tbody>
                        <tr>
                            <th>氏名</th>
                            <td><c:out value="${report.employee.name}" /></td>
                        </tr>
                        <tr>
                            <th>日付</th>
                            <td><fmt:formatDate value="${report.report_date}"
                                    pattern="yyyy-MM-dd" /></td>
                        </tr>
                        <tr>
                            <th>内容</th>
                            <td>
                                <pre><c:out value="${report.content}" /></pre>
                            </td>
                        </tr>
                        <tr>
                            <th>登録日時</th>
                            <td><fmt:formatDate value="${report.created_at}"
                                    pattern="yyyy-MM-dd HH:mm:ss" /></td>
                        </tr>
                        <tr>
                            <th>更新日時</th>
                            <td><fmt:formatDate value="${report.updated_at}"
                                    pattern="yyyy-MM-dd HH:mm:ss" /></td>
                        </tr>
                    </tbody>
                </table>
                <!-- ログインしているEmployeeのidと、日報のEmployeeのidが同じ場合にeditへのリンク  -->
                <c:if test="${sessionScope.login_employee.id == report.employee.id}">
                    <p>
                        <a href="<c:url value='/reports/edit?id=${report.id}' />">この日報を編集する</a>
                    </p>
                </c:if>
            </c:when>
            <c:otherwise>
                <h2>お探しのデータは見つかりませんでした。</h2>
            </c:otherwise>
        </c:choose>
        <p>
            <a href="<c:url value='/reports/index' />">日報一覧へ</a>
        </p>
        <p>
            <a href="<c:url value='/reports/likes_list' />">いいねを押した日報一覧へ</a>
        </p>
    </c:param>
</c:import>