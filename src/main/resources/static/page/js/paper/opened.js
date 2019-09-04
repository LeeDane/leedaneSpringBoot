/**
**主题模板的渲染
*/
function render(paper){
    if(!paper)
        return;

    //处理标题
    if(!isEmpty(paper.title)){
        $("#paper-title").html(paper.title);
    }

    //处理称呼
    if(!isEmpty(paper.call)){
        $("#paper-call").html(paper.call);
    }

    //处理正文
    if(!isEmpty(paper.body)){
        $("#paper-body").html(paper.body);
    }

    //处理祝颂语
    if(!isEmpty(paper.greeting)){
       $("#paper-signature-greeting").html(paper.greeting);
    }

    //处理署名名字
     if(!isEmpty(paper.signatureName)){
            $("#paper-signature-name").html(paper.signatureName);
     }
     //处理署名时间
      if(!isEmpty(paper.signatureTime)){
             $("#paper-signature-time").html(paper.signatureTime);
      }

}