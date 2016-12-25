
<%--
  Created by IntelliJ IDEA.
  User: jansing
  Date: 16-12-19
  Time: 下午3:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>Title</title>
    <%@ include file="/WEB-INF/head.jsp"%>
</head>
<body style="align-content:center">
index从0开始，
<c:forEach items="${fileList}" var="file" varStatus="status">
    <c:choose>
        <c:when test="${fn:contains(file, '.')}">
            <a href="${ctx}/view?fileId=${status.index}" target="_blank">index=${status.index} - ${file}</a>
        </c:when>
        <c:otherwise>
            ${file}
        </c:otherwise>
    </c:choose><br/>
</c:forEach>
</body>
</html>
