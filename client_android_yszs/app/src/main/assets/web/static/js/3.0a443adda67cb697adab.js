webpackJsonp([3],{"/oed":function(e,t){},HMF5:function(e,t,s){"use strict";var r=s("4YfN"),n=s.n(r),i={props:{value:String,index:Number},data:function(){return{personNumberArr:["不限人数","1人","2人","3人","4人","5人","6人","7人","8人","9人","10人","11人","12人","13人","14人","15人","16人","17人","18人","19人","20人","21人","22人","22人+"]}},mounted:function(){},methods:{choose:function(e,t){this.$emit("input",e),this.$emit("update:index",t)}}},o={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"person-number-wrapper"},e._l(e.personNumberArr,function(t,r){return s("span",{key:r,staticClass:"person-number-item",class:{"item-choose":t===e.value},on:{click:function(s){return e.choose(t,r)}}},[e._v("\n    "+e._s(t)+"\n  ")])}),0)},staticRenderFns:[]};var a=s("C7Lr")(i,o,!1,function(e){s("O2dZ")},null,null).exports,c=s("9ER+"),l={props:{value:String},computed:{},data:function(){return{amTimeArr:["10:30","11:00","11:30","12:00","12:30","13:00","13:30","14:00"],pmTimeArr:["16:30","17:00","17:30","18:00","18:30","19:00","20:00","21:00"]}},methods:{choose:function(e){this.$emit("input",e)}}},m={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"time-choose-wrapper"},[s("div",{staticClass:"title"},[e._v(">中餐时间：")]),e._v(" "),s("div",{staticClass:"time-choose-list-wrapper"},e._l(e.amTimeArr,function(t,r){return s("span",{key:r,staticClass:"list-item",class:t===e.value?"item-selected":"",on:{click:function(s){return e.choose(t)}}},[e._v("\n      "+e._s(t)+"\n    ")])}),0),e._v(" "),s("div",{staticClass:"title"},[e._v(">晚餐时间：")]),e._v(" "),s("div",{staticClass:"time-choose-list-wrapper"},e._l(e.pmTimeArr,function(t,r){return s("span",{key:r,staticClass:"list-item",class:t===e.value>-1?"item-selected":"",on:{click:function(s){return e.choose(t)}}},[e._v("\n      "+e._s(t)+"\n    ")])}),0)])},staticRenderFns:[]};var u=s("C7Lr")(l,m,!1,function(e){s("WLbA")},null,null).exports,v=s("fUgm"),p={components:{PersonNumberChoose:a,TimeChoose:u},computed:n()({minDate:function(){return new Date},maxDate:function(){return new Date(+new Date+2592e6)}},Object(v.b)(["reservationDate","reservationTime","reservationPersonNumber","reservationPersonNumberText"])),data:function(){return{dateTime:"",showPersonNumber:!1,personNumberText:"",personNumber:0,time:"",showTime:!1}},watch:{time:function(e){this.showTime=!1,this.$store.commit("SET_RESERVATION_TIME",e)},personNumber:function(e){this.$store.commit("SET_RESERVATION_PERSON_NUMBER",e)},personNumberText:function(e){this.showPersonNumber=!1,this.$store.commit("SET_RESERVATION_PERSON_NUMBER_TEXT",e)},dateTime:function(e){this.$store.commit("SET_RESERVATION_DATE",e)}},mounted:function(){this.dateTime=this.reservationDate||Object(c.f)(new Date),this.time=this.reservationTime,this.personNumber=this.reservationPersonNumber,this.personNumberText=this.reservationPersonNumberText}},d={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"index-form-wrapper"},[s("div",{staticClass:"line"},[s("span",{staticClass:"title"},[e._v("就餐日期：")]),e._v(" "),s("mu-date-input",{staticClass:"date-picker",attrs:{hintText:"请选择就餐日期",container:"bottomSheet","label-float":"","full-width":"","value-format":"YYYY-MM-DD",minDate:e.minDate,maxDate:e.maxDate},model:{value:e.dateTime,callback:function(t){e.dateTime=t},expression:"dateTime"}})],1),e._v(" "),s("div",{staticClass:"line"},[s("span",{staticClass:"title"},[e._v("就餐时间：")]),e._v(" "),s("input",{directives:[{name:"model",rawName:"v-model",value:e.time,expression:"time"}],staticClass:"time-choose-bar",attrs:{placeholder:"请选择就餐时间",readonly:""},domProps:{value:e.time},on:{click:function(t){e.showTime=!0},input:function(t){t.target.composing||(e.time=t.target.value)}}})]),e._v(" "),s("div",{staticClass:"line"},[s("span",{staticClass:"title"},[e._v("就餐人数：")]),e._v(" "),s("input",{directives:[{name:"model",rawName:"v-model",value:e.personNumberText,expression:"personNumberText"}],staticClass:"person-choose-bar",attrs:{placeholder:"请选择就餐人数",readonly:""},domProps:{value:e.personNumberText},on:{click:function(t){e.showPersonNumber=!0},input:function(t){t.target.composing||(e.personNumberText=t.target.value)}}})]),e._v(" "),s("mu-dialog",{staticClass:"dialog-wrapper",attrs:{title:"选择就餐人数",width:"320",open:e.showPersonNumber},on:{"update:open":function(t){e.showPersonNumber=t}}},[s("person-number-choose",{attrs:{index:e.personNumber},on:{"update:index":function(t){e.personNumber=t}},model:{value:e.personNumberText,callback:function(t){e.personNumberText=t},expression:"personNumberText"}})],1),e._v(" "),s("mu-dialog",{staticClass:"dialog-wrapper",attrs:{title:"选择就餐时间",width:"320",open:e.showTime},on:{"update:open":function(t){e.showTime=t}}},[s("time-choose",{model:{value:e.time,callback:function(t){e.time=t},expression:"time"}})],1)],1)},staticRenderFns:[]};var h=s("C7Lr")(p,d,!1,function(e){s("/oed")},null,null);t.a=h.exports},IAfb:function(e,t,s){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r=s("lC5x"),n=s.n(r),i=s("J0Oq"),o=s.n(i),a=s("4YfN"),c=s.n(a),l=s("fUgm"),m=s("HMF5"),u=s("gyMJ"),v=s("iLhm"),p=s("4TJr"),d={components:{IndexForm:m.a,TopayBar:v.a,TicketCollector:p.a},computed:c()({},Object(l.b)(["reservationDate","reservationTime","reservationPersonNumber","reservationDetail","reservationBoxItem","collector"])),data:function(){return{isShowMessage:!1,messageText:""}},created:function(){this.$store.commit("GET_COLLECTOR")},methods:{showMessage:function(){var e=this;return o()(n.a.mark(function t(){return n.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,Object(u.m)({category:"restaurant"});case 2:e.messageText=t.sent,e.isShowMessage=!0;case 4:case"end":return t.stop()}},t,e)}))()},makeOrder:function(){var e=this;return o()(n.a.mark(function t(){var s,r;return n.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return(s=e.reservationDetail).collector=e.collector,s.queryDate=e.reservationDate,s.timeQuantum=e.reservationTime,s.restaurantId=e.reservationDetail.id,s.boxId=e.reservationBoxItem.id,s.customerCount=e.reservationPersonNumber,t.next=9,Object(u._0)(s);case 9:r=t.sent,e.$store.commit("SET_COLLECTOR",{value:e.collector}),e.$router.push({path:e.$route.path+"/payment",query:{orderNo:r.orderNo}});case 12:case"end":return t.stop()}},t,e)}))()}}},h={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("main-page",{staticClass:"reservation-booking-container",attrs:{back:"",title:"订餐信息"}},[s("template",{slot:"scroll"},[s("div",{staticClass:"block-section detail-wrapper"},[s("div",{staticClass:"name"},[e._v(e._s(e.reservationDetail.name))]),e._v(" "),s("div",{staticClass:"box-item-wrapper"},[s("img",{attrs:{src:e.reservationBoxItem.image}}),e._v(" "),s("div",{staticClass:"right-content"},[s("div",{staticClass:"item-name"},[e._v(e._s(e.reservationBoxItem.name||e.reservationBoxItem.specs))]),e._v(" "),s("div",{staticClass:"item-sub-info"},[e._v(e._s(e.reservationBoxItem.area)+" | "+e._s(e.reservationBoxItem.orientation)+" | "+e._s(e.reservationBoxItem.description)+" | "+e._s(e.reservationBoxItem.type))])])])]),e._v(" "),s("div",{staticClass:"block-section"},[s("index-form")],1),e._v(" "),s("div",{staticClass:"block-section"},[s("ticket-collector",{attrs:{type:"restaurant",placeholderName:"请输入订餐预订人姓名",placeholderMobile:"请输入订餐预订人联系方式"},model:{value:e.collector,callback:function(t){e.collector=t},expression:"collector"}})],1),e._v(" "),s("div",{directives:[{name:"is-link",rawName:"v-is-link"}],staticClass:"block-section message-wrapper",on:{click:e.showMessage}},[s("i",{staticClass:"iconfont icon-wenhao"}),e._v(" "),s("div",{staticClass:"message-btn"},[e._v("预订须知")])])]),e._v(" "),s("topay-bar",{attrs:{slot:"bottom"},on:{commit:e.makeOrder},slot:"bottom",model:{value:e.reservationBoxItem.deposit,callback:function(t){e.$set(e.reservationBoxItem,"deposit",t)},expression:"reservationBoxItem.deposit"}},[s("div",{staticClass:"topay-bar-inner-wrapper"},[s("div",{staticClass:"tips"},[e._v("订金可抵餐费")]),e._v(" "),e.reservationDetail.discount?s("div",{staticClass:"discount"},[e._v("\n        到店可享受"),s("span",{staticClass:"discount-text"},[e._v(e._s(e.reservationDetail.discount))]),e._v("优惠")]):e._e()])]),e._v(" "),s("mu-dialog",{staticClass:"dialog-wrapper",attrs:{title:"预订须知",width:"320",open:e.isShowMessage},on:{"update:open":function(t){e.isShowMessage=t}}},[s("div",{domProps:{innerHTML:e._s(e.messageText)}})])],2)},staticRenderFns:[]};var f=s("C7Lr")(d,h,!1,function(e){s("d2S6")},null,null);t.default=f.exports},O2dZ:function(e,t){},WLbA:function(e,t){},d2S6:function(e,t){}});