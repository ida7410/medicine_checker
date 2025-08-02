<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <!-- bootstrap -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css" integrity="sha384-xOolHFLEh07PJGoPkLv1IbcEPTNtaed2xpHsD9ESMhqIYd0nLMwNLD69Npy4HI+N" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.7.1.js" integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-Fy6S3B9q64WdZWQUiU+q4/2Lc9npb8tCaSX9FK7E8HnRr0Jz8D6OP9dO5Vg3Q9ct" crossorigin="anonymous"></script>
    <!-- datepicker-->
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
    <!-- my stylesheet -->
    <link rel="stylesheet" href="/static/css/style.css">
    <style>
        #chat-box {
            height: 500px;
            overflow-y: auto;
            padding: 1rem;
            border: 1px solid #dee2e6;
            background-color: #f8f9fa;
            border-radius: 0.5rem;
        }
        .message {
            margin-bottom: 1rem;
        }
        .user-msg {
            text-align: right;
        }
        .user-msg .card {
            background-color: #d0ebff;
        }
        .bot-msg .card {
            background-color: #e6f4ea;
        }
        .card {
            display: inline-block;
            padding: 0.5rem 1rem;
            border-radius: 1rem;
            max-width: 80%;
        }
    </style>
</head>
<body class="bg-light">

<div class="container py-5">
    <h2 class="mb-4 text-center">💬 Gemini Chat Assistant</h2>

    <div id="chat-box" class="mb-3"></div>

    <form id="chat-form" class="d-flex gap-2">
        <input type="text" id="question" class="form-control" placeholder="Ask something..." required>
        <button type="submit" class="btn btn-primary">Send</button>
    </form>
</div>

<script>
    $(document).ready(function () {
        $('#chat-form').submit(function (e) {
            e.preventDefault();
            const question = $('#question').val();
            $('#question').val('');
            console.log(question)

            $('#chat-box').append('<div class="message user-msg"><div class="card text-end"><strong>You:</strong>'
                + question + '</div></div>');

            $.ajax({
                url: '/gemini/generate',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ question: question }),
                success: function (response) {
                    $('#chat-box').append('<div class="message bot-msg"><div class="card"><strong>Gemini:</strong> ' +
                        response + '</div></div>');
                    $('#chat-box').scrollTop($('#chat-box')[0].scrollHeight);
                },
                error: function () {
                    $('#chat-box').append('<div class="message bot-msg"><div class="card bg-danger text-white">' +
                        '<strong>Error:</strong> Could not get a response from Gemini.</div></div>');
                }
            });
        });
    });
</script>

</body>
</html>