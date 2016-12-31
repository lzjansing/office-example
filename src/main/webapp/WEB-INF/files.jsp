
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
<table>
    <tr><th>file</th><th>- LINUX -</th><th>- WINDOWS -</th></tr>
<c:forEach items="${fileList}" var="file" varStatus="status">
    <tr>
        <td>${file}</td>
    <c:choose>
        <c:when test="${fn:contains(fn:substring(file, fn:length(file)-5, fn:length(file)), '.')}">
            <td><a href="javascript:void(0);" onclick="callcovert(${status.index});">- LINUX -</a></td>
            <td><a href="javascript:void(0);" onclick="callowa(${status.index});">- WINDOWS -</a></td>
        </c:when>
        <c:otherwise>
        </c:otherwise>
    </c:choose>
    </tr>
</c:forEach>
</table>
<br/>
<form id="callform" action="">
    <input type="hidden" name="fileId" id="fileId"/>
    LINUX ：<input type="text" name="convertServletPath" value="http://127.0.0.1:8098/libre/view"/><br/>
    WINDOWS：<input type="text" name="owaServerPath" value="http://owa.etop.edu.cn"/><br/>
</form>
<script type="application/javascript">
    function callowa(fileId){
        var fileInput = document.getElementById("fileId");
        fileInput.value=fileId;
        var callform = document.getElementById("callform");
        callform.action="${ctx}/wopi/files/view";
        callform.submit();
        return false;
    }
    function callcovert(fileId){
        var fileInput = document.getElementById("fileId");
        fileInput.value=fileId;
        var callform = document.getElementById("callform");
        callform.action="${ctx}/view";
        callform.method="get";
        callform.submit();
        return false;
    }
</script>
</body>
</html>
