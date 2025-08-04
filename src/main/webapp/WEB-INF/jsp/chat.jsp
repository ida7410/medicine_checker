<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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

    </style>
</head>
<body class="bg-light">

<div class="container py-5">
    <h2 class="mb-4 text-center">💬 Gemini Chat Assistant</h2>
    <a href="/reset-chat-cookie">resetcookie</a>
    <div id="chat-box" class="mb-3">
    <c:forEach items="${chatsMapList}" var="chatMap">
    <c:if test="${chatMap.role == 'user'}">
        <div class="message user-msg">
            <div class="card text-end">
                <strong>You: </strong> ${chatMap.content}
            </div>
        </div>
    </c:if>
    <c:if test="${chatMap.role == 'model'}">
        <div class="message bot-msg">
            <div class="card">
                <strong>Gemini: </strong> <p>${chatMap.content}</p>
                <a href="${chatMap.downloadIcsUrl}" class="btn btn-outline-success btn-sm mt-2" download>📥 Download .ics File </a>
            </div>
        </div>
    </c:if>
    </c:forEach>
    </div>

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

            $('#chat-box').append(
                '<div class="message user-msg">' +
                    '<div class="card text-end">' +
                        '<strong>You: </strong>' + question +
                '</div></div>');

            $.ajax({
                url: '/gemini/generate',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ question: question }),
                success: function (response) {
                    $('#chat-box').append(
                        '<div class="message bot-msg">' +
                            '<div class="card">' +
                            '<strong>Gemini: </strong> ' +
                            '<p>' + response["answer"] + '<p>' +
                            '<a href="' + response["downloadIcsUrl"] + '" class="btn btn-outline-success btn-sm mt-2" download>📥 Download .ics File </a>' +
                        '</div></div>');
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


<%--{ "candidates":
    [ { "content":
    { "parts":
    [ { "text":
    "Okay, that's good that you're taking Vitamin D! Here's some helpful information and things to consider:\n\n**Important Considerations:**\n\n* **Dosage:** The recommended daily dose of vitamin D varies depending on your age, health conditions, and current vitamin D levels. **It's crucial to talk to your doctor or a registered dietitian to determine the appropriate dosage for you.** Taking too much vitamin D can lead to toxicity.\n* **Form:** Vitamin D comes in two main forms:\n * **Vitamin D2 (ergocalciferol):** Usually derived from plant sources.\n * **Vitamin D3 (cholecalciferol):** Produced in your skin when exposed to sunlight and also available from animal sources. D3 is generally considered more effective at raising vitamin D levels in the blood.\n* **With Food:** Vitamin D is a fat-soluble vitamin, meaning it's best absorbed when taken with a meal that contains some fat. This helps your body absorb it more efficiently.\n* **Consistency:** Taking it every morning helps establish a routine and makes it more likely you'll remember to take it.\n* **Timing:** While morning is a good choice, the most important thing is to take it consistently at the same time each day.\n* **Underlying Conditions and Medications:** Certain medical conditions (e.g., kidney disease, malabsorption issues) and medications can affect vitamin D absorption or metabolism. Discuss your vitamin D supplementation with your doctor, especially if you have any health conditions or are taking other medications.\n\n**Tips for Remembering to Take Your Vitamin D:**\n\n* **Pair it with another morning routine:** Take it with breakfast, after brushing your teeth, or when you take other medications.\n* **Keep it visible:** Place the bottle of vitamin D in a prominent location where you'll see it every morning, such as on your kitchen counter or bathroom sink.\n* **Use a pill organizer:** Fill a pill organizer with your daily dose of vitamin D for the week.\n* **Set a reminder:** Use your phone or an app to set a daily reminder to take your vitamin D.\n* **Track it:** Use a habit tracker app or a simple calendar to mark off each day you take your vitamin D.\n\n**Why is Vitamin D Important?**\n\nVitamin D plays a vital role in many bodily functions, including:\n\n* **Bone health:** Helps your body absorb calcium, which is essential for strong bones.\n* **Immune function:** Supports a healthy immune system.\n* **Muscle function:** Contributes to muscle strength and function.\n* **Cell growth:** Plays a role in cell growth and development.\n* **Mood regulation:** May help regulate mood and reduce the risk of depression.\n\n**Disclaimer:** I am an AI chatbot and cannot provide medical advice. Always consult with a qualified healthcare professional for any health concerns or before making any decisions related to your health or treatment.\n" } ],
     "role": "model" },
     "finishReason": "STOP",
     "avgLogprobs": -0.12810764955670645 } ],
      "usageMetadata":
      { "promptTokenCount": 7,
      "candidatesTokenCount": 623,
      "totalTokenCount": 630,
      "promptTokensDetails":
      [ { "modality": "TEXT",
      "tokenCount": 7 } ],
      "candidatesTokensDetails":
      [ { "modality": "TEXT",
      "tokenCount": 623 } ] },
      "modelVersion": "gemini-2.0-flash",
      "responseId": "yGqOaPGwCMyFn9kPlovd-AI"
      }--%>