<#--noinspection JSIgnoredPromiseFromCall-->
<#-- @ftlvariable name="target" type="be.bluexin.cutemaid.database.User" -->
<html>
<head>
    <#include "include/head.ftl">
    <title>User ${(target.name)!"not found"} - MDCafe Hub</title>
</head>
<body>
<#include "include/header.ftl">
<section class="section">
    <div class="container">
    <#if target??>
        <#if user??>
            <#assign edit=target.id.value == user.id.value>
            <#if edit>
            <script src="/static/js/edituser.js"></script>
            </#if>
        <#else>
            <#assign edit=false>
        </#if>
        <article class="media">
            <figure class="media-left">
                <lightbox src="${target.avatarUrl}" class="is-128x128"></lightbox>
            </figure>
            <div class="content">
                <h1 id="name_display" class="title">${target.name}</h1>
                <#if edit>
                    <div class="field has-addons">
                        <div class="control">
                            <a class="button is-medium">
                                Theme
                            </a>
                        </div>
                        <div class="dropdown is-hoverable control">
                            <div class="dropdown-trigger">
                                <button class="button is-medium" aria-haspopup="true" aria-controls="dropdown-menu">
                                    <span>${user.theme.pretty()}</span>
                                    <span class="icon is-small">
                                        <i class="fas fa-angle-down" aria-hidden="true"></i>
                                    </span>
                                </button>
                            </div>
                            <div class="dropdown-menu" id="dropdown-menu" role="menu">
                                <div class="dropdown-content">
                                    <#list user.theme.declaringClass.enumConstants as theme>
                                        <a class="dropdown-item<#if theme == user.theme> is-active</#if>"
                                           onclick="setTheme(${user.id}, '${theme.name()}')">
                                            <strong>${theme.pretty()}</strong> : ${theme.description}<br/>
                                        </a>
                                    </#list>
                                </div>
                            </div>
                        </div>
                    </div>

                </#if>
            </div>
            <br/>
        </article>
    <#else>
        <section class="section">
            <h1 class="title container">User not found.</h1>
        </section>
    </#if>
    </div>
</section>
<#include "include/footer.ftl">
</body>
</html>
