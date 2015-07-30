<#assign pageTitle = "Create a new post">
<#include "fragments/header.ftl">

<#include "fragments/navbar.ftl">

<div class="container">
  <form action="/newpost" method="POST">
    ${errors!""}
    <h2>Title</h2>

    <input type="text" name="subject" size="120" value="${subject!""}" /><br />

    <h2>Blog Entry</h2>
    <h2><textarea name="body" cols="120" rows="20">${body!""}</textarea><br /></h2>

    <h2>Tags</h2>
    Comma separated, please<br />
    <input type="text" name="tags" size="120" value="${tags!""}" /><br />

    <p><input type="submit" value="Submit" /></p>
</div>

<#include "fragments/footer.ftl">
