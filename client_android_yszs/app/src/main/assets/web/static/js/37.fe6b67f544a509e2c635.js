webpackJsonp([37],{AZWP:function(e,t){},IJ5e:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var s=n("lC5x"),i=n.n(s),o=n("J0Oq"),r=n.n(o),a=n("4YfN"),c=n.n(a),l=n("gyMJ"),p=n("fUgm"),d=n("h/jM"),u={computed:c()({title:function(){return 0===this.step?"绑定手机号码":"填写验证码"}},Object(p.b)(["wxUserInfo"])),data:function(){return{step:0,telephone:"",code:"",binded:!1}},mounted:function(){this.wxUserInfo.mobile&&(this.telephone=this.wxUserInfo.mobile,this.binded=!0)},methods:{sendCode:function(){var e=this;return r()(i.a.mark(function t(){return i.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:if(e.wxUserInfo.unionid){t.next=3;break}return e.$toast("当前状态无法绑定"),t.abrupt("return");case 3:return t.next=5,Object(l._9)({mobile:e.telephone});case 5:e.step=1;case 6:case"end":return t.stop()}},t,e)}))()},confirmBind:function(){var e=this;return r()(i.a.mark(function t(){var n,s;return i.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return n={mobile:e.telephone,verifyCode:e.code,unionid:e.wxUserInfo.unionid},Object(d.b)()&&(n.openType="wechat"),t.next=5,Object(l.d)(n);case 5:(s=t.sent).token&&(e.$store.commit("LOGIN_SUCCESS",s.token),e.wxUserInfo.token=s.token),e.$store.commit("SET_WXUSERINFO",c()({},e.wxUserInfo,{mobile:e.telephone})),e.$toast("绑定成功！"),setTimeout(function(){e.$router.go(-1)},1e3);case 10:case"end":return t.stop()}},t,e)}))()},preStep:function(){--this.step},rebind:function(){this.binded=!1,this.telephone=""}}},v={render:function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("main-page",{staticClass:"bind-telephone-container",attrs:{title:e.title,back:""}},[0!==e.step?n("div",{attrs:{slot:"header-right"},on:{click:e.preStep},slot:"header-right"},[e._v("上一步")]):e._e(),e._v(" "),0===e.step?n("div",{staticClass:"section-wrapper"},[e.binded?[n("div",{staticClass:"telephone-wrapper"},[n("div",{staticClass:"input binded"},[e._v(e._s(e.telephone))])]),e._v(" "),n("div",{staticClass:"tips"},[n("b",[e._v("Tips:")]),e._v(" 手机号码已绑定，如需替换绑定手机请点击重新绑定")]),e._v(" "),n("div",{staticClass:"button",on:{click:e.rebind}},[e._v("重新绑定")])]:[n("div",{staticClass:"telephone-wrapper"},[n("l-input",{staticClass:"input",attrs:{placeholder:"请输入绑定的手机号码",type:"number"},model:{value:e.telephone,callback:function(t){e.telephone=t},expression:"telephone"}})],1),e._v(" "),n("div",{staticClass:"tips"},[n("b",[e._v("Tips:")]),e._v(" 绑定手机号码可以方便用户查询订单数据，请绑定订票常用手机号码")]),e._v(" "),n("div",{staticClass:"button",on:{click:e.sendCode}},[e._v("发送验证码")])]],2):1===e.step?n("div",{staticClass:"section-wrapper"},[n("div",{staticClass:"telephone-code-wrapper"},[n("l-input",{staticClass:"input",attrs:{placeholder:"请填入接收到的验证码",type:"number",maxLength:6},model:{value:e.code,callback:function(t){e.code=t},expression:"code"}})],1),e._v(" "),n("div",{staticClass:"tips"},[n("b",[e._v("Tips:")]),e._v(" 请耐心等待平台发送的验证码")]),e._v(" "),n("div",{staticClass:"button",on:{click:e.confirmBind}},[e._v("确认绑定")])]):e._e()])},staticRenderFns:[]};var h=n("C7Lr")(u,v,!1,function(e){n("AZWP")},null,null);t.default=h.exports}});