<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
   <meta name="viewport" content="width=1,initial-scale=1,user-scalable=1" />
   <title>Coop Join</title>
   <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
   <link href="http://fonts.googleapis.com/css?family=Lato:100italic,100,300italic,300,400italic,400,700italic,700,900italic,900" rel="stylesheet" type="text/css">
   <link rel="stylesheet" type="text/css" href="/Coop/res/bootstrap/css/bootstrap.min.css" />
   <link rel="stylesheet" type="text/css" href="/Coop/res/css/styles.css" />
   
   <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>

   <section class="container">
         <section class="login-form">
            <form method="post" role="login">
               <img src="/Coop/res/images/logo.png" class="img-responsive" alt="" />
               <input type="text" name="id" placeholder="Id" required class="form-control input-lg" />
               <input type="password" name="password" placeholder="Password" required class="form-control input-lg" />
               <input type="text" name="name" placeholder="Name" required class="form-control input-lg" />
               <input type="email" name="email" placeholder="Email" required class="form-control input-lg" />
               <button type="submit" name="go" class="btn btn-lg btn-primary btn-block">Regist</button>
               <div>
                  <p>Welcome to join us</p>
                  <c:if test="${ not empty msg }">
                        <div class="alert alert-error">${ msg }</div>
                  </c:if>
               </div>
            </form>
            <div class="form-links">
               <a href="#">www.Coop.com</a>
            </div>
         </section>
   </section>
   
   <script src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
   <script src="/Coop/res/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>