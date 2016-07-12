<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="cdc" %>

<cdc:page title="Listagem de Produtos">
	${msg}
	<sec:authorize access="isAuthenticated()">
	<sec:authentication property="principal" var="user"/>
		<div>
			<spring:message code="user.welcome" arguments="${user.name}" />
		</div>
	</sec:authorize>
	
	<sec:authorize access="hasRole('ROLE_ADMIN')">
		<c:url value="/products/form" var="formLink"></c:url>
		<a href="${formLink}">
			Cadastrar novo produto
		</a>
	</sec:authorize>
	
	<table>
		<tr>
			<th>Título</th>
			<th>Descrição</th>
			<th>Valores</th>
		</tr>
		<c:forEach items="${products}" var="product">
			<tr>
				<td>${product.title}</td>
				<td>${product.description}</td>
				<c:forEach items="${product.prices}" var="price">
					<td>[${price.value} - ${price.bookType}]</td>
				</c:forEach>
				<td>
					<c:url value="/products/${product.id}" var="linkDetalhar" />
					<a href="${linkDetalhar}">Detalhar</a>
				</td>
			</tr>
		</c:forEach>
	</table>
</cdc:page>