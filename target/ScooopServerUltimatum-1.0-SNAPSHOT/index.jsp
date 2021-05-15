<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
    <h1><%= "Hello World!" %>
    </h1>
    <br/>
    <a href="hello-servlet">Hello Servlet</a>
    <a href="login.jsp">Login Servlet</a>
    <a href="checkSession.jsp">Check Session</a>
    <button onclick="logout()">Logout</button>
    <hr/>
    <form action="api/auth/change-pwd" method="post">
        Target: <input type="number" name="uid" required />
        <br/>
        Your password: <input type="text" name="current_pwd" required />
        <br/>
        Target's new password: <input type="text" name="new_pwd" required />
        <br/>
        Target's new password confirm: <input type="text" name="confirm_pwd" required />
        <br/>
        <input type="submit" value="change" />
    </form>
    <hr/>
    <form action="api/auth/add" method="post">
        Username: <input type="text" name="usr" />
        <br/>
        Password: <input type="text" name="pwd" />
        <br/>
        Privilege: <input type="number" name="privilege" />
        <br/>
        <input type="submit" value="add" />
    </form>
    <hr/>
    <form action="api/auth/remove" method="post">
        uid: <input type="text" name="uid" />
        <br/>
        <input type="submit" value="remove" />
    </form>
    <hr/>
    <button onclick="current()">Fetch current orders</button>
    <hr/>
    <form action="api/order/get" method="get">
        order id: <input type="text" name="id" />
        <br/>
        <input type="submit" value="get" />
    </form>
    <hr/>
    table: <input type="text" name="id" id="addtable" />
    <button onclick="add()">Add order</button>
    <hr/>
    <form action="api/order/remove" method="post">
        order id: <input type="text" name="id" />
        <br/>
        <input type="submit" value="remove" />
    </form>
    <hr/>
    <button onclick="update()">Update order</button>
    <hr/>
    <form action="api/order/check" method="post">
        order id: <input type="text" name="id" />
        <br/>
        <input type="submit" value="check" />
    </form>
    <hr/>
    <form action="api/stats" method="get">
        from date: <input type="text" name="from" />
        <br/>
        to date: <input type="text" name="to" />
        <br/>
        <input type="submit" value="get stats" />
    </form>
    <hr/>
    <button onclick="items()">Get all items</button>
    <script>
        function logout() {
            fetch('api/auth/logout', {
                method: 'POST',
                headers: {
                    'content-type' : 'application/x-www-form-urlencoded; charset=UTF-8'
                },
                body: 'logout=true'
            }).then(
                (response) => {
                    if(response.status === 200)
                    {
                        alert('successfully logged out')
                    }
                    else
                    {
                        alert('logout failed')
                    }
                }
            )
        }

        function current() {
            fetch('api/order/current')
            .then(response => response.json())
            .then(data => {
                console.log(data)
            })
        }

        function add() {
            let data = {
                table: document.getElementById("addtable").value,
                items: [
                    {
                        id: 1,
                        count: 3
                    },
                    {
                        id: 4,
                        count: 1
                    }
                ]
            }
            let dataJson = JSON.stringify(data);
            fetch('api/order/add', {
                method: 'POST',
                headers: {
                    'content-type' : 'application/x-www-form-urlencoded; charset=UTF-8'
                },
                body: 'data=' + dataJson
            })
            .then(response => console.log(response))
            .then(data => {
                console.info(data)
            })
        }

        function update() {
            let data = {
                orderId: 2,
                table: "b2",
                items: [
                    {
                        id: 1,
                        count: 3
                    },
                    {
                        id: 2,
                        count: -1
                    },
                    {
                        id: 5,
                        count: 1
                    }
                ]
            }
            let dataJson = JSON.stringify(data);
            fetch('api/order/update', {
                method: 'POST',
                headers: {
                    'content-type' : 'application/x-www-form-urlencoded; charset=UTF-8'
                },
                body: 'data=' + dataJson
            })
                .then(response => console.log(response))
        }

        function items() {
            fetch('api/items')
                .then(response => response.json())
                .then(data => {
                    console.log(data)
                })
        }
    </script>
</body>
</html>