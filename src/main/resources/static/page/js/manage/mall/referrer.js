layui.use(['form'], function(){
    form = layui.form;
    //对已经生成推荐码的，获取推荐关系图
    if($("#has-referrer-code").length > 0){
        var loadi = layer.load(2, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        $.ajax({
            type : "GET",
            url : "/my/manage/mall/referrer/relation",
            dataType: 'json',
            success : function(data) {
                layer.close(loadi);
                if(data.success){
                    // 基于准备好的容器(这里的容器是id为chart1的div)，初始化echarts实例
                    myChart = echarts.init(document.getElementById("my-chart"));
                    var graph = {};
                    graph.nodes = data.message.nodes;
                    graph.links = data.message.links;
                    var categories = [{name: "上上级", itemStyle: {color: "rgb(0,134,139)"}},
                                       {name: "上级", itemStyle: {color: "rgb(123,104,238)"}},
                                       {name: "您", itemStyle: {color: "rgb(255,0,0)"}},
                                       {name: "下级", itemStyle: {color: "rgb(205,92,92)"}},
                                       {name: "下下级", itemStyle: {color: "rgb(255，0，255)"}}
                                     ];
                    graph.nodes.forEach(function (node) {
                        node.value = node.symbolSize;
                        node.draggable = true;
                        node.label = "dddd";
                        //node.fixed = true;
                        delete node["attributes"];
                        delete node["sourceId"];
                        delete node["parentId"];
                        delete node["x"];
                        delete node["y"];
                        delete node["z"];
                        delete node["value"];
                   });

//                     graph.links.forEach(function (link) {});
                   console.log(JSON.stringify(graph));
                   option = {
                       title: {
                           text: '推荐关系图',
                           subtext: 'Default layout',
                           top: 'bottom',
                           left: 'right'
                       },
                       tooltip: {},
                       legend: [{
                           // selectedMode: 'single',
                           data: categories.map(function (a) {
                               return a.name;
                           })
                       }],
                       animation: false,
                       series : [
                           {
                               name: '与您关系',
                               type: 'graph',
                               layout: 'force',
                               data: graph.nodes,
                               links: graph.links,
                               categories: categories,
                               roam: true,
                               label: {
                                   position: 'right'
                               },
                               force: {
                                   repulsion: 250
                               }
                           }
                       ]
                   };
                   myChart.setOption(option);
                }else{
                    ajaxError(data);
                }
            },
            error : function(data) {
                layer.close(loadi);
                ajaxError(data);
            }
        });
    }
});

/**
**
** 绑定推荐人
**/
function bindReferrer(){
    var code = $("#referrer-code").val();
    if(isEmpty(code)){
        layer.confirm('您没填写推荐码，是否默认绑定系统管理员做为您的推荐人？', {
            btn: ['绑定管理员','考虑一下'] //按钮
        }, function(){
            doBind(code);
        }, function(){
            layer.tips("请先填写正确的推荐码", "#referrer-code",{tips:[3,'#FFB800'], time: 3000});
        });
        return;
    }

    doBind(code);
}

/**
** 执行绑定操作
**/
function doBind(code){
    var loadi = layer.load(2, {
        shade: [0.1,'#fff'] //0.1透明度的白色背景
    });
    $.ajax({
        type : "POST",
        url : "/my/manage/mall/referrer/bind",
        data: {code: code},
        dataType: 'json',
        success : function(data) {
            layer.close(loadi);
            if(data.success){
                //发送成功，页面开始倒计时
                layer.msg(data.message +"，2秒后自动刷新");
                reloadPage(2000);
            }else{
                ajaxError(data);
            }
        },
        error : function(data) {
            layer.close(loadi);
            ajaxError(data);
        }
    });
}

/**
** 生成推荐码
**/
function buildReferrerCode(){
    //必须绑定推荐人才能操作
    if($("#referrer-code").length > 0){
        layer.tips("请先绑定推荐人后才操作", "#referrer-code",{tips:[3,'#FFB800'], time: 3000});
        return;
    }
    var loadi = layer.load(2, {
        shade: [0.1,'#fff'] //0.1透明度的白色背景
    });
    $.ajax({
        type : "POST",
        url : "/my/manage/mall/referrer/code",
        dataType: 'json',
        success : function(data) {
            layer.close(loadi);
            if(data.success){
                //发送成功，页面开始倒计时
                layer.msg(data.message +" 2秒后自动刷新");
                reloadPage(2000);
            }else{
                ajaxError(data);
            }
        },
        error : function(data) {
            layer.close(loadi);
            ajaxError(data);
        }
    });
}