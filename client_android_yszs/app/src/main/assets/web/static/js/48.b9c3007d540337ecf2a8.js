webpackJsonp([48],{"43UG":function(t,i){},aKbL:function(t,i,e){"use strict";Object.defineProperty(i,"__esModule",{value:!0});var s={components:{TravelLineSection:e("GN/t").a},data:function(){return{navList:[{title:"舟山群岛",iconText:"舟",iconColor:"#E8D489",image:"static/images/ship-zs.png",type:"zs"},{title:"宁波",iconText:"宁",iconColor:"#DB9C84",image:"static/images/ship-nb.png",type:"nb",isDeveloping:!0},{title:"温州",iconText:"温",iconColor:"#9B81E1",image:"static/images/ship-wz.png",type:"wz",url:"http://ceshi.zjsajgw.com"},{title:"台州",iconText:"台",iconColor:"#DBF099",image:"static/images/ship-tz.png",type:"tz",isDeveloping:!0}],showDialog:!1}},methods:{toShipTicket:function(t){var i=this;t.isDeveloping?this.$toast("该功能尚在开发中，敬请期待"):"zs"===t.type?(this.showDialog=!0,setTimeout(function(){i.showDialog=!1,i.$router.push({name:"ShipTicketIndex",params:{area:t.type}})},1e3)):t.url?window.location.href=t.url:this.$router.push({name:"ShipTicketIndex",params:{area:t.type}})}}},a={render:function(){var t=this,i=t.$createElement,e=t._self._c||i;return e("main-page",{staticClass:"ship-ticket-nav-wrapper",attrs:{noHeader:""}},[e("template",{slot:"scroll"},[e("l-header-image",{attrs:{type:"ship",back:""}}),t._v(" "),e("section",{staticClass:"ship-ticket-nav block-section"},t._l(t.navList,function(i,s){return e("div",{key:s,staticClass:"ship-ticket-nav-item",on:{click:function(e){return t.toShipTicket(i)}}},[e("div",{staticClass:"icon",style:{backgroundColor:i.iconColor}},[e("img",{attrs:{src:i.image}})]),t._v(" "),e("span",[t._v(t._s(i.title))])])}),0),t._v(" "),e("travel-line-section")],1),t._v(" "),e("l-dialog",{attrs:{show:t.showDialog,preventClose:""},on:{"update:show":function(i){t.showDialog=i}}},[e("div",{staticClass:"dialog-msg"},[t._v("正在接入舟山市水上客运官方平台...")])])],2)},staticRenderFns:[]};var o=e("C7Lr")(s,a,!1,function(t){e("43UG")},null,null);i.default=o.exports}});