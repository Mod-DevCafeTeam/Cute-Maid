<#-- @ftlvariable name="error" type="io.ktor.http.HttpStatusCode" -->
<html>
<head>
    <#include "include/head.ftl">
    <title>Error ${error.value} - MDCafe Hub</title>
</head>
<body>
<#include "include/header.ftl">
<section class="section">
    <h1 class="title container">An error has occurred: ${error}</h1>
</section>
<#include "include/footer.ftl">
</body>
</html>
