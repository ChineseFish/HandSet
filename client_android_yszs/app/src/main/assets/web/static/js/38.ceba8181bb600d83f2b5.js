webpackJsonp([38],{KyJ4:function(t,e){},oJm2:function(t,e,s){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=s("lC5x"),a=s.n(i),n=s("J0Oq"),o=s.n(n),r=s("aA9S"),u=s.n(r),l=s("4YfN"),c=s.n(l),m=s("N3LN"),d=s("/9Wm"),p=s("wBUC"),b=s("gyMJ"),f=s("fUgm"),h={components:{DatepickBar:m.a,BusItemCanbuy:d.a,BusItemDisable:d.a,ListSortBar:p.a,ListSortBarItem:p.b},props:{from:{type:String,default:""},to:{type:String,default:""},time:{type:[String,Number]}},data:function(){return{flag:1,pickedDate:this.time,isLoaded:!1,dialogIsShow:!1,msgContent:"",minTicketAmounts:1,timeToDep:30,allBusList:[],busListCanBuy:[],busListDisable:[],sortSelected:0}},computed:c()({},Object(f.b)(["queryHistory","queryDate","canWebBuyDays"]),{lastHistory:function(){var t=this.queryHistory.planebus;return t&&t.slice(-1)[0]},travel:function(){return"机场大巴"},start:function(){return this.from||this.lastHistory&&this.lastHistory.start.cname},end:function(){return this.to||this.lastHistory&&this.lastHistory.end.cname}}),mounted:function(){this.pickedDate=this.time||this.queryDate,this.getPlaneBusList()},methods:{toBusOrder:function(t){if(t.notBookable)this.$store.commit("SHOW_DIALOG","\n          <p><b>注：该线路不支持网上预订</b></p>\n          <p><b>线路：</b>"+t.routeName+'</p>\n          <p>\n            <b>机场热线：</b>\n            <a href="tel://'+t.fromStationTelephone+'">'+t.fromStationTelephone+"</a>\n          </p>\n        ");else{var e=t.disableReason;if(e)this.$store.commit("SHOW_DIALOG","该班次"+e+",请选择当日其他班次或换个日期试试");else{var s=u()({},t,{queryDate:this.pickedDate,handlingFee:t.handlingFee||2,category:"plane-bus",limit:t.orderTicketCount<t.surplusTicket?t.orderTicketCount:t.surplusTicket});this.$store.commit("SET_DETAILDATA",{type:"bus",value:s}),this.$router.push({path:"plane-bus-list/order"})}}},getPlaneBusList:function(){var t=this;return o()(a.a.mark(function e(){var s,i;return a.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:if(t.start&&t.end&&t.pickedDate){e.next=2;break}return e.abrupt("return");case 2:return s={date:t.pickedDate,sCity:t.start,eCity:t.end},t.isLoaded=!1,e.next=6,Object(b._7)(s);case 6:i=e.sent,t.allBusList=i,t.busListCanBuy=t.filterBusList(t.allBusList,!0),t.busListDisable=t.filterBusList(t.allBusList,!1),t.isLoaded=!0;case 11:case"end":return e.stop()}},e,t)}))()},filterBusList:function(t,e){var s=this,i=Date.now(),a=this.pickedDate.replace(/-/g,"/");return t.filter(function(t){var n=t.fromTime,o=new Date(a+" "+n.substring(0,5)).getTime(),r=t.showTicketInfo.match(/^[0-9]+/),u=r&&r[0];return t.surplusTicket=u||"",!(e||!t.notBookable)||(e?u>s.minTicketAmounts&&o-i>=60*s.timeToDep*1e3:o<=i?(t.disableReason="已发车",!1):o-i<=60*s.timeToDep*1e3?(t.disableReason="暂停售票",!0):u<=s.minTicketAmounts&&(t.disableReason="无票",!0))})},showDialog:function(t){this.dialogIsShow=!0,this.msgContent=t},touchSort:function(){this.$refs["main-page"].scrollToTop(0)}},watch:{pickedDate:function(t){this.getPlaneBusList()}}},y={render:function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("main-page",{ref:"main-page",staticClass:"bus-ticket-list-container",attrs:{title:t.travel,isLoading:!t.isLoaded,data:t.allBusList,back:""}},[s("datepick-bar",{attrs:{category:"plane-bus",canWebBuyDays:t.canWebBuyDays},model:{value:t.pickedDate,callback:function(e){t.pickedDate=e},expression:"pickedDate"}}),t._v(" "),s("section",{staticClass:"bus-ticket-list-wrapper",attrs:{slot:"scroll"},slot:"scroll"},[s("div",t._l(t.busListCanBuy,function(e,i){return s("bus-item-canbuy",{key:i,attrs:{depTime:e.fromTime,startPort:e.fromStationName,endPort:e.toStationShow||e.toStationName,price:e.fullPrice,busType:e.busType,amount:e.showTicketInfo},nativeOn:{click:function(s){return t.toBusOrder(e)}}})}),1),t._v(" "),s("div",t._l(t.busListDisable,function(e,i){return s("bus-item-disable",{key:i,attrs:{depTime:e.fromTime,startPort:e.fromStationName,endPort:e.toStationShow||e.toStationName,price:e.fullPrice,busType:e.busType,disable:"",reason:e.disableReason},nativeOn:{click:function(s){return t.toBusOrder(e)}}})}),1)]),t._v(" "),s("list-sort-bar",{attrs:{slot:"bottom",list:t.busListCanBuy},slot:"bottom",model:{value:t.sortSelected,callback:function(e){t.sortSelected=e},expression:"sortSelected"}},[s("list-sort-bar-item",{attrs:{sortKey:"fromTime",sortText:"时间",icon:"time-xianxing"},nativeOn:{click:function(e){return t.touchSort(e)}}}),t._v(" "),s("list-sort-bar-item",{attrs:{sortKey:"fullPrice",sortText:"价格",icon:"caiwu-xianxing"},nativeOn:{click:function(e){return t.touchSort(e)}}})],1),t._v(" "),s("l-dialog",{staticClass:"disable-tip-dialog-wrapper",attrs:{show:t.dialogIsShow},on:{"update:show":function(e){t.dialogIsShow=e}}},[s("p",{staticClass:"msg-content"},[t._v(t._s(t.msgContent))])])],1)},staticRenderFns:[]};var k=s("C7Lr")(h,y,!1,function(t){s("KyJ4")},null,null);e.default=k.exports}});