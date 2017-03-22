#说在前面的话
  > 提醒：自2016-7-14日起，将不维护spring+struts2+hibernate这个项目，原来SSH框架项目地址[点击这里](https://github.com/LeeDane/appServer)，今后只开发spring+springmvc+mybatis组合框架实现版 

#前言
  > 大家好，这个是本人日常学习的项目--java服务器端源码。目前先把原形做出来，很多代码还没有来得及重构，阅读的时候难免有问题，希望看到兄弟能多多指正，共同学习。android app端地址[请点击这里](https://github.com/LeeDane/app)

#主要使用到的技术
  * Websocket+redis聊天室
  * Jsoup实现对网站的爬去和解析
  * Redis对用户信息/文章/心情等进行缓存
  * 用springMVC来支持restful接口规范
  * Solr实现对用户/文章/心情的全文检索
  * Rabbitmq提供消息队列
  * Echarts展示分析结果的图表
  * Ueditor富文本编辑器作用于大文本和图片的编辑

##功能描述
  1. 每天定时抓取网易新闻和散文网首页的文章，并入库和添加到索引中。
  2. 积分管理模块：用户签到、发送弹屏消息扣积分等等。
  3. 博客模块：查看博客列表(滚动到底部自动加载更多，所有访问用户可以操作)、修改博客(博客主和管理员可以操作)、编写博客(普通用户需要审核，管理员用户直接发布)、对博客的其他管理(如删除、收藏、关注等)
  4. 个人中心模块：管理个人的基本信息、签到、对个人心情的增删改查等等操作以及对发表的心情提供转发、评论和设置是否能评论/能转发/私有、点赞、翻译等功能。
  5. 图库模块：提供瀑布流式查看模式，添加图库以及点击放大图片等功能。
  6. 记账模块：对应于app端的记账，提供列表查看，echart图表分析统计展示，新增和编辑新的记账记录的功能。
  7. 登录模块：提供生成二维码给app进行扫码安全登录、简单注册和登录校验等。
  8. 全文搜索模块：提供对用户/文章/心情的全文检索。
  8. 微信公众号：目前已经支持在线翻译、与机器人聊天、查询博客、发布app心情，获取最新博客列表、获取最新app发布的版本等(2016-06-06日前)

##效果截图
![](http://7xnv8i.com1.z0.glb.clouddn.com/leedaneweb-github-001.png?imageView/2/w/800/q/90)

![](http://7xnv8i.com1.z0.glb.clouddn.com/leedaneweb-github-002.png?imageView/2/w/800/q/90)

![](http://7xnv8i.com1.z0.glb.clouddn.com/leedaneweb-github-003.png?imageView/2/w/800/q/90)

![](http://7xnv8i.com1.z0.glb.clouddn.com/leedaneweb-github-004.png?imageView/2/w/800/q/90)

![](http://7xnv8i.com1.z0.glb.clouddn.com/leedaneweb-github-005.png?imageView/2/w/800/q/90)

![](http://7xnv8i.com1.z0.glb.clouddn.com/leedaneweb-github-006.png?imageView/2/w/800/q/90)

![](http://7xnv8i.com1.z0.glb.clouddn.com/leedaneweb-github-007.png?imageView/2/w/800/q/90)

![](http://7xnv8i.com1.z0.glb.clouddn.com/leedaneweb-github-008.png?imageView/2/w/800/q/90)

![](http://7xnv8i.com1.z0.glb.clouddn.com/leedaneweb-github-010.png?imageView/2/w/800/q/90)




