<template>
  <div>
     <!-- 无限滚动 -->
     <van-list
        v-model="loading_vanlist"
        :finished="finished"
        finished-text="没有更多了"
        @load="debouncedOnLoad"
        offset=0
        >

        <div v-for="(column, index) in tableData"
        :key="index">
              <comment
              :product_id="product_id"
              :scope=column
              :amisub="false"
              fatherid="0"
              style="margin: 20px auto 0;"
            />
            <!--底部线条-->
            <div style="margin: 8px 20px 3px 45px;border-bottom: 1px solid rgba(128,128,128,0.2);height: 1px;"></div>
        </div>

    </van-list>



  </div>
</template>

<script>
//import { api5 } from '@/api/myapi'
import comment from './components/comment'
// import HelloWorld from '@/components/HelloWorld.vue'
import axios from '@/utils'
import { List} from 'vant';
import { throttle } from 'lodash';

export default {
  components: {
    // HelloWorld
    comment,
    'van-list':List,
  },
  props:{
    product_id:String,
    value2:String
  },
  data () {
    return {
      product_id_fast_local:this.product_id,
      tableData: [],
      total: 0, //总个数
      current:0, // 当前页数
      pages:0, // 总页数
      loading_vanlist:false,
      finished: false,
      currentPage:0,
      PageSize:6,
      debouncedOnLoad:null
    }
  },
  //
  //
  //
  //
  methods: {
    // 此方法只适用父dropdown_closed被调用
    async dropdown_closed(product_id) {
      //var oldScrollPosition
      console.log("重新获取,参数"+product_id+"props"+this.product_id) //参数传的没这么快 要手动
      if(product_id)this.product_id_fast_local = product_id
      // 重新初始化一些数据
      this.tableData = []
      this.total = 0
      this.current = 0
      this.pages= 0
      this.loading_vanlist=false
      this.finished=false
      this.currentPage=0
      this.PageSize=6

      this.onLoad()
     
    },
    
    // vant手机划到底部时触发
    async onLoad() {
      //var oldScrollPosition
      console.log("滚到底部，触发加载")
      // 异步更新数据
      // setTimeout 仅做示例，真实场景中一般为 ajax 请求
      this.currentPage +=1
      await axios.get('comment/page2',{
        params: {
          currentPage: this.currentPage,
          PageSize: this.PageSize,
          product_id:this.product_id_fast_local,
          value2:this.value2,
          father_comment_id:"0"
        }
      }).then(response=>{
        //oldScrollPosition = window.pageYOffset
        if(response.data.code){
          //var oldScrollPosition = window.pageYOffset
          //this.tableData = response.data.data.records
          this.tableData.splice(this.tableData.length, 0, ...response.data.data.records);
          //setTimeout(() => {window.scrollTo(0, oldScrollPosition)}, 0);
          this.total = response.data.data.total
          this.current = response.data.data.current
          this.pages = response.data.data.pages
        }else{
          this.$message.error(response.data.msg)
        }

        console.log(response)
      }).catch(error=>{
        console.log(error)
      })

      // 加载状态结束
      this.loading_vanlist = false;

      // 当前页数大于等于总页数
      //if (this.PageSize >= this.TotalPage) {
      if (this.current >= this.pages) {
          this.finished = true;
      }
      

    },
    
  },

  //
  //
  created () {
    // 节流OnLoad 1._.throttle 每n秒钟只调用一次 2._.debounce n秒之后无操作 调用
    this.debouncedOnLoad = throttle(this.onLoad, 1000);
  }
}
</script>

<style scoped>
.testcss{
  width: 85%;
  margin: 0 auto 0 auto;
  border-radius: 10px;
  min-width: 250px;
}
.block{
  margin-top: 60px;
  margin-bottom: 20px;
}
.block2{
  margin-top: 10px;
  display: none;
}
@media screen and (max-width: 750px){
  .block{
  display: none;
  }
  .block2{
  display:block;
  }
  .testcss{
    width: 95%;
  }
}

</style>

<style scoped>
  .el-row {
    margin-bottom: 20px;
    &:last-child {
      margin-bottom: 0;
    }
  }
  .el-col {
    border-radius: 4px;
  }

  .row-bg {
    padding: 10px 0;
    background: #d3dce6;
  }

  .pbzero{
    padding-bottom: 0;
  }

</style>
