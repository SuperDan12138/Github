**MVVM + Jetpack + Coroutine + Hybrid架构组件的Github客户端。**

**支持功能:**
1.用户无需登录此应用程序即可浏览流行存储库(home页)
2.用户可以浏览GitHub存储库repo页，点击item中的头像或者项目，可以通过webView + html5框架加载个人详情页或项目详情页，无需启动其他浏览器(repo页)
3.用户可以根据直接搜索编程语言或通过stars或更新时间搜索存储库  (home->右上角进入search->右上角有筛选条件(stars或updated时间))
4.用户可以登录到他/她的GitHub帐户并到达配置文件的存储库列表, 身份验证状态在应用程序启动之间保留, 用户可以注销并返回匿名状态。(login页  已经在local.properties填写了有效token，点击sign in能直接登录)
5.经过身份验证的用户可以对其存储库提出问题。 (repo 页登录态  长按item可提交issue)
6.支持横竖屏切换

7.Compose暂未接入
****
****

