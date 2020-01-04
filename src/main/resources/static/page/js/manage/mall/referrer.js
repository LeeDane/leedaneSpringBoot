layui.use(['form'], function(){
    form = layui.form;

    // 基于准备好的容器(这里的容器是id为chart1的div)，初始化echarts实例
    myChart = echarts.init(document.getElementById("my-chart"));
    myChart.showLoading();
    $.get('http://127.0.0.1:8089/les_miserables.gexf', function (xml) {
        myChart.hideLoading();

        var graph = echarts.dataTool.gexf.parse(xml);
        var categories = [{name: "上上级", itemStyle: {color: "rgb(0,134,139)"}},
                            {name: "上级", itemStyle: {color: "rgb(123,104,238)"}},
                            {name: "您", itemStyle: {color: "rgb(255,0,0)"}},
                            {name: "下级", itemStyle: {color: "rgb(205,92,92)"}},
                            {name: "下下级", itemStyle: {color: "rgb(255,248,220)"}}];
        /*for (var i = 0; i < 5; i++) {
            categories[i] = {
                name: '类目' + i
            };
        }*/
        graph.nodes.forEach(function (node) {
//            node.itemStyle = null;
            /*if(node.name == "Myriel"){
                node.symbolSize = 15;
            }else{
                node.symbolSize = 10;
            }*/

            node.value = node.symbolSize;
            node.category = node.attributes.modularity_class;

            // Use random x, y
//            node.x = node.y = null;
            node.draggable = true;
            node.label = "dddd";
            //node.fixed = true;
            delete node["attributes"];
        });

        graph.links.forEach(function (link) {
        //            node.itemStyle = null;
                    /*if(node.name == "Myriel"){
                        node.symbolSize = 15;
                    }else{
                        node.symbolSize = 10;
                    }*/

//                    node.value = node.symbolSize;
//                    node.category = node.attributes.modularity_class;

                    // Use random x, y
        //            node.x = node.y = null;
//                    node.draggable = true;
//                    node.label = "dddd";
//                    delete node["attributes"];
                    //link.value = "value144";
                    link.lineStyle = {color : '#ff0000', width: 2}; //设置连线的颜色、线的宽度
                });
        console.log(JSON.stringify(graph));
//        graph = JSON.parse('{"nodes":[{"id":"0","name":"上上级","itemStyle":{"normal":{"color":"rgb(0,134,139)"}},"symbolSize":14,"attributes":{"modularity_class":0},"value":14,"category":0,"draggable":true},{"id":"1","name":"上级","itemStyle":{"normal":{"color":"rgb(123,104,238)"}},"symbolSize":18,"attributes":{"modularity_class":1},"value":18,"category":1,"draggable":true},{"id":"2","name":"您","itemStyle":{"normal":{"color":"rgb(255,0,0)"}},"symbolSize":25,"attributes":{"modularity_class":2},"value":25,"category":2,"draggable":true},{"id":"3","name":"下级1","itemStyle":{"normal":{"color":"rgb(205,92,92)"}},"symbolSize":12,"attributes":{"modularity_class":3},"value":12,"category":3,"draggable":true},{"id":"4","name":"下级2","itemStyle":{"normal":{"color":"rgb(205,92,92)"}},"symbolSize":12,"attributes":{"modularity_class":3},"value":12,"category":3,"draggable":true},{"id":"5","name":"下级3","itemStyle":{"normal":{"color":"rgb(205,92,92)"}},"symbolSize":12,"attributes":{"modularity_class":3},"value":12,"category":3,"draggable":true},{"id":"6","name":"下级4","itemStyle":{"normal":{"color":"rgb(205,92,92)"}},"symbolSize":12,"attributes":{"modularity_class":3},"value":12,"category":3,"draggable":true},{"id":"7","name":"下级5","itemStyle":{"normal":{"color":"rgb(205,92,92)"}},"symbolSize":12,"attributes":{"modularity_class":3},"value":12,"category":3,"draggable":true},{"id":"8","name":"下下级1-1","itemStyle":{"normal":{"color":"rgb(255,248,220)"}},"symbolSize":12,"attributes":{"modularity_class":4},"value":12,"category":4,"draggable":true},{"id":"9","name":"下下级1-2","itemStyle":{"normal":{"color":"rgb(255,248,220)"}},"symbolSize":10,"attributes":{"modularity_class":4},"value":10,"category":4,"draggable":true},{"id":"10","name":"下下级1-3","itemStyle":{"normal":{"color":"rgb(255,248,220)"}},"symbolSize":10,"attributes":{"modularity_class":4},"value":10,"category":4,"draggable":true},{"id":"11","name":"下下级1-4","itemStyle":{"normal":{"color":"rgb(255,248,220)"}},"symbolSize":10,"attributes":{"modularity_class":4},"value":10,"category":4,"draggable":true},{"id":"12","name":"下下级1-5","itemStyle":{"normal":{"color":"rgb(255,248,220)"}},"symbolSize":10,"attributes":{"modularity_class":4},"value":10,"category":4,"draggable":true},{"id":"13","name":"下下级2-1","itemStyle":{"normal":{"color":"rgb(255,248,220)"}},"symbolSize":10,"attributes":{"modularity_class":4},"value":10,"category":4,"draggable":true},{"id":"14","name":"下下级2-2","itemStyle":{"normal":{"color":"rgb(255,248,220)"}},"symbolSize":10,"attributes":{"modularity_class":4},"value":10,"category":4,"draggable":true},{"id":"15","name":"下下级3-1","itemStyle":{"normal":{"color":"rgb(255,248,220)"}},"symbolSize":10,"attributes":{"modularity_class":4},"value":10,"category":4,"draggable":true},{"id":"16","name":"下下级4-1","itemStyle":{"normal":{"color":"rgb(255,248,220)"}},"symbolSize":10,"attributes":{"modularity_class":4},"value":10,"category":4,"draggable":true},{"id":"17","name":"下下级5-1","itemStyle":{"normal":{"color":"rgb(255,248,220)"}},"symbolSize":10,"attributes":{"modularity_class":4},"value":10,"category":4,"draggable":true},{"id":"18","name":"下下级5-2","itemStyle":{"normal":{"color":"rgb(255,248,220)"}},"symbolSize":10,"attributes":{"modularity_class":4},"value":10,"category":4,"draggable":true},{"id":"19","name":"下下级5-3","itemStyle":{"normal":{"color":"rgb(255,248,220)"}},"symbolSize":10,"attributes":{"modularity_class":4},"value":10,"category":4,"draggable":true},{"id":"20","name":"下下级5-4","itemStyle":{"normal":{"color":"rgb(255,248,220)"}},"symbolSize":10,"attributes":{"modularity_class":4},"value":10,"category":4,"draggable":true}],"links":[{"id":"0","name":null,"source":"1","target":"0","lineStyle":{"normal":{}}},{"id":"1","name":null,"source":"2","target":"1","lineStyle":{"normal":{}}},{"id":"2","name":null,"source":"3","target":"2","lineStyle":{"normal":{}}},{"id":"3","name":null,"source":"4","target":"2","lineStyle":{"normal":{}}},{"id":"4","name":null,"source":"5","target":"2","lineStyle":{"normal":{}}},{"id":"5","name":null,"source":"6","target":"2","lineStyle":{"normal":{}}},{"id":"6","name":null,"source":"7","target":"2","lineStyle":{"normal":{}}},{"id":"7","name":null,"source":"8","target":"3","lineStyle":{"normal":{}}},{"id":"8","name":null,"source":"9","target":"3","lineStyle":{"normal":{}}},{"id":"9","name":null,"source":"10","target":"3","lineStyle":{"normal":{}}},{"id":"13","name":null,"source":"11","target":"3","lineStyle":{"normal":{}}},{"id":null,"name":null,"source":"12","target":"3","lineStyle":{"normal":{}}},{"id":"11","name":null,"source":"13","target":"4","lineStyle":{"normal":{}}},{"id":"10","name":null,"source":"14","target":"4","lineStyle":{"normal":{}}},{"id":"14","name":null,"source":"15","target":"5","lineStyle":{"normal":{}}},{"id":"15","name":null,"source":"16","target":"6","lineStyle":{"normal":{}}},{"id":"16","name":null,"source":"17","target":"7","lineStyle":{"normal":{}}},{"id":"17","name":null,"source":"18","target":"7","lineStyle":{"normal":{}}},{"id":"18","name":null,"source":"19","target":"7","lineStyle":{"normal":{}}},{"id":"19","name":null,"source":"20","target":"7","lineStyle":{"normal":{}}}]}');
        option = {
            title: {
                text: 'Les Miserables',
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
                    name: 'Les Miserables',
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
                        repulsion: 100
                    }
                }
            ]
        };

        myChart.setOption(option);
    }, 'xml');
});

/**
**
** 绑定推荐人
**/
function bindReferrer(){
    var code = $("#referrer-code").val();
    if(isEmpty(code)){
        layer.tips("请先填写正确的推荐码", "#referrer-code",{tips:[3,'#FFB800'], time: 3000});
        return;
    }

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