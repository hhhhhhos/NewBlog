<template>
  <div>
  
    <div v-if="!this.$store.state.IsMobile">
      
      <h1>历史订单</h1>
      
      <div style="margin: 50px 100px 150px 100px;" class="myborder">
        <el-table
        v-loading=IsTableLoading
        :data="tableData"
        style="width: 100%;background-color: white;">

        <el-table-column
          type="expand"
          width="25">
          <template slot-scope="props">
            <div style="margin: 0 30px;">
              <ET1 :Datas="props.row.info" :columns="columns1" :showHeader="false"></ET1>
              <el-descriptions title="收货信息" style="margin: 20px 0 20px 20px;">
                  <el-descriptions-item label="姓名">{{ props.row.address.name }}</el-descriptions-item>
                  <el-descriptions-item label="手机号">{{ props.row.address.phone }}</el-descriptions-item>
                  <el-descriptions-item label="居住地">{{props.row.address.info?.[0]}}</el-descriptions-item>
                  <el-descriptions-item label="备注">
                    <el-tag v-if="props.row.status==='未支付'" size="small">{{props.row.status}}</el-tag>
                    <el-tag v-else type="success" size="small">{{props.row.status}}</el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="收货地址">{{ props.row.address.info?.[0]+props.row.address.info?.[1]+props.row.address.info?.[2]+props.row.address.detail }}</el-descriptions-item>
                </el-descriptions>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          v-for="(column, index) in this.columns"
          :key="index"
          :prop="column.prop"
          :label="column.label"
          :width="column.width"
          :align="column.align"
          >
          <template slot-scope="scope" >
            <div v-if="column.label==='时间'">{{ tableData[scope.$index][column.prop].replace(/T/g, ' ') }}</div>
            <div v-if="column.label==='订单ID'">{{ tableData[scope.$index][column.prop]}}</div>
            <div v-if="column.label==='总额'">{{ tableData[scope.$index][column.prop]}}</div>
            <div v-if="column.label==='件数'">{{ tableData[scope.$index][column.prop]}}</div>
            <div v-if="column.label==='信息'">
              <span v-for="(data, index) in tableData[scope.$index][column.prop]" :key="index">
                {{ data.product.name }} &nbsp;
              </span>
            </div>
            <div v-if="column.label==='状态'">
              <el-tag v-if="tableData[scope.$index].status==='未支付'" style="cursor: pointer;" @click="gotopay(tableData[scope.$index].status,tableData[scope.$index].id,tableData[scope.$index].totalMoney,tableData[scope.$index].totalNum,tableData[scope.$index].info?.[0].product.name)">{{ tableData[scope.$index][column.prop]}}
              </el-tag>
              <el-tag v-else type="success" style="cursor: pointer;" @click="gotopay(tableData[scope.$index].status,tableData[scope.$index].id,tableData[scope.$index].totalMoney,tableData[scope.$index].totalNum,tableData[scope.$index].info?.[0].product.name)">{{ tableData[scope.$index][column.prop]}}
              </el-tag>
            </div>
            <div v-if="column.label==='操作'">
              <el-button @click="confirmtodelete(tableData[scope.$index].id,tableData[scope.$index].status)" size="mini" type="danger" icon="el-icon-delete" circle></el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[5, 10, 15, 20]"
          :page-size=PageSize
          layout="total, sizes, prev, pager, next, jumper"
          :total="TotalPage">
      </el-pagination>

      <!-- 弹出框 --> 
      <el-dialog :title="this.dialog_title" :visible.sync="dialogVisible" width="300px" center>
        <div style="display: flex; justify-content: center; align-items: center;">
          <el-radio v-model="radio2" label="1">余额(剩余:{{ this.obj.money }})</el-radio>
          <el-radio v-model="radio2" label="2">支付宝</el-radio>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="final_pay">确定</el-button>
        </span>
      </el-dialog>

      </div>

    </div>

    <div v-else>
      <van-list
        v-model="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
        offset=-40
      >
      <!-- 滚动条与底部距离小于 offset 时触发load事件 -->

        <el-table
          v-loading=IsTableLoading
          :data="tableData"
          style="width: 100vw;background-color: white;"
          ref="expandTable"
          @row-click="handleRowClick">

          <el-table-column
            type="expand"
            width="25">
            <template slot-scope="props">
              <div style="margin: 0 30px;">
                <ET1 :Datas="props.row.info" :columns="columns1" :showHeader="false"></ET1>
                <el-descriptions title="收货信息" style="margin: 20px 20px 20px 5px;">
                    <el-descriptions-item label="姓名">{{ props.row.address.name }}</el-descriptions-item>
                    <el-descriptions-item label="手机号">{{ props.row.address.phone }}</el-descriptions-item>
                    <el-descriptions-item label="居住地">{{props.row.address.info?.[0]}}</el-descriptions-item>
                    <el-descriptions-item label="备注">
                      <el-tag v-if="props.row.status==='未支付'" size="small">{{props.row.status}}</el-tag>
                      <el-tag v-else type="success" size="small">{{props.row.status}}</el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="收货地址">{{ props.row.address.info?.[0]+props.row.address.info?.[1]+props.row.address.info?.[2]+props.row.address.detail }}</el-descriptions-item>
                  </el-descriptions>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            v-for="(column, index) in this.columns"
            :key="index"
            :prop="column.prop"
            :label="column.label"
            :width="column.width"
            :align="column.align"
            >
            <template slot-scope="scope" >
              <div>
                <div v-if="column.label==='时间'">{{ tableData[scope.$index][column.prop].replace(/T/g, ' ') }}</div>
                <div v-if="column.label==='订单ID'">{{ tableData[scope.$index][column.prop]}}</div>
                <div v-if="column.label==='总额'">{{ tableData[scope.$index][column.prop]}}</div>
                <div v-if="column.label==='件数'">{{ tableData[scope.$index][column.prop]}}</div>
                <div v-if="column.label==='信息'">
                  <span v-for="(data, index) in tableData[scope.$index][column.prop]" :key="index">
                    {{ data.product.name }} &nbsp;
                  </span>
                </div>
                <div v-if="column.label==='状态'">
                  <el-tag v-if="tableData[scope.$index].status==='未支付'" style="cursor: pointer;" @click.stop="gotopay(tableData[scope.$index].status,tableData[scope.$index].id,tableData[scope.$index].totalMoney,tableData[scope.$index].totalNum,tableData[scope.$index].info?.[0].product.name)">{{ tableData[scope.$index][column.prop]}}
                  </el-tag>
                  <el-tag v-else type="success" style="cursor: pointer;" @click.stop="gotopay(tableData[scope.$index].status,tableData[scope.$index].id,tableData[scope.$index].totalMoney,tableData[scope.$index].totalNum,tableData[scope.$index].info?.[0].product.name)">{{ tableData[scope.$index][column.prop]}}
                  </el-tag>
                </div>
                <div v-if="column.label==='操作'">
                  <el-button @click="confirmtodelete(tableData[scope.$index].id,tableData[scope.$index].status)" size="mini" type="danger" icon="el-icon-delete" circle></el-button>
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>

      </van-list>


      <!-- 弹出框 --> 
      <el-dialog :title="this.dialog_title" :visible.sync="dialogVisible" width="80vw" center>
        <div style="display: flex; justify-content: center; align-items: center;">
          <el-radio v-model="radio2" label="1">余额(剩余:{{ this.obj.money }})</el-radio>
          <el-radio v-model="radio2" label="2">支付宝</el-radio>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="final_pay">确定</el-button>
        </span>
      </el-dialog>

    </div>

  </div>
</template>

<script>
import axios from '@/utils';
import ET1 from '/src/components/ElTable_buylist_result'
import { List} from 'vant';

export default {
  props: {
    columns:Array,
    geturl:String,
    PtableData:Array
  },
  components:{
    'van-list':List,
    //'van-cell':Cell,
    ET1
  },
  data() {
    return{
      // region vant手机参数
      loading: false,
      finished: false,
      // end region
      tableData:[],
      columns1 : [
        //{ prop: 'id', label: 'ID', width: '100' },
        //{ prop: 'user_id', label: '用户ID', width: '100' },
        //{ prop: 'product_id', label: '商品ID', width: '100' },
        { prop: 'photo', label: '商品图', width: '180' },
        { prop: 'name', label: '商品名' },
        { prop: 'product_num', label: '购买数量' , width: '120' },
        { prop: 'price', label: '价格', width: '100' },
        { prop: 'create_time', label: '创建时间', width: '180' },
      ],
      currentPage:1,
      PageSize:10,
      TotalPage:null,
      IsTableLoading:true,
      // 弹窗参数
      dialogVisible:false,
      dialog_title:"支付方式",
      radio2:"1",
      obj:{
        "id": null,
        "name": null,
        "age": null,
        "sex": null,
        "addresses": null,
        "phone": null,
        "create_time": Date,
        "password": null,
        "money":null
      },
      alipay:{
        "id":null,
        "money":null,
        "num":null,
        "name":null
      },
    }
  },
  methods:{
    handleRowClick(row) {
      row.expanded = !row.expanded;
      this.$refs.expandTable.toggleRowExpansion(row, row.expanded);
    },
    handleExpand(props) {
      // 可以在这里控制展开逻辑
      // `props` 包含了行数据，`event` 是触摸事件对象
      // 使用 props.row.toggleRowExpansion() 来展开或收起行
      console.log("handleExpand")
      props.row.toggleRowExpansion();
    },
    // vant手机无限滚动范例
    async onLoad() {
      //var oldScrollPosition
      console.log("滚到底部，触发加载")
      // 异步更新数据
      // setTimeout 仅做示例，真实场景中一般为 ajax 请求
      this.PageSize +=3
      await axios.get(this.geturl,{
        params: {
          currentPage: this.currentPage,
          PageSize: this.PageSize
        }
      }).then(response=>{
        if(response.data.code===0)this.$message.error(response.data.msg)
        else {
          //oldScrollPosition = window.pageYOffset
          this.tableData = response.data.data.records
          // 防止element-table移动视角
          //setTimeout(() => {window.scrollTo(0, oldScrollPosition),this.loading1 = false}, 0);
          this.loading1 = false
          this.TotalPage = response.data.data.total
          this.IsTableLoading = false
          console.log(response)
        }
      }).catch(error=>{
        this.$message.error(error.data.msg)
        console.log(error)
      })

      // 加载状态结束
      this.loading = false;

      // 数据全部加载完成
      if (this.PageSize >= this.TotalPage) {
          this.finished = true;
      }
      

    },
    // 拿表
    gettable(){
      axios.get(this.geturl,{
        params: {
          currentPage: this.currentPage,
          PageSize: this.PageSize
        }
      }).then(response=>{
        if(response.data.code===0)this.$message.error(response.data.msg)
        else {
          this.tableData = response.data.data.records
          this.TotalPage = response.data.data.total
          this.IsTableLoading = false
          console.log(response)
          
        }
      }).catch(error=>{
        this.$message.error(error.data.msg)
        console.log(error)
      })

    },
    // 页容量变化
    handleSizeChange(val) {
      this.PageSize = val
      this.gettable()
    },
    // 切页
    handleCurrentChange(val) {
      this.currentPage = val
      this.gettable()
    },
    // 点击删除
    confirmtodelete(id,status){
      this.$confirm("确定删除订单吗", '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          if(status==="未支付")this.confirmtobackbuylist(id)
          else this.deletebyid(id)
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        });
    },
    // 是否返回购物车
    confirmtobackbuylist(id){
      console.log(id)
      this.$confirm("是否将商品返回购物车", '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.deletebyid(id,true)
        }).catch(() => {
          this.deletebyid(id)
          this.$message({
            type: 'info',
            message: '不返回购物车并删除'
          })
        });
    },
    //删订单
    deletebyid(orderid,backbuylist = false){
      // 根据id删除 请求范例 //是否返回车
      axios.delete('/order/deletebyid',{params: {D_id:orderid,backBuyList:backbuylist}})
      .then(response=>{
        if(response.data.code){
          this.$message.success(response.data.data)
          setTimeout(() => {window.location.reload()}, 500);
        }
        else this.$message.error(response.data.msg)
        console.log(response)
      }).catch(error=>{
        this.$message.error(error.data.msg);
        console.log(error)
      })

    },
    // 下面是为了弹窗搞的
    // 判断状态,是否跳转支付
    gotopay(status,id,money,num,name){
      if(status==="未支付"){
        
        this.alipay.id = id
        this.alipay.money = money
        this.alipay.num = num
        this.alipay.name = name

        this.dialogVisible = true
      }else if(status==="已支付"){
        this.$message("订单已支付")
      }else{
        this.$message.error("状态错误")
      }
    },
    // 点确定支付
    async final_pay(){
      console.log(this.radio2)
      // 判断跳转余额支付还是支付宝支付 this.radio2=1 余额 2支付宝
      if(this.radio2==="1"){
        // 余额支付
        await this.moneypay(this.alipay.id)
        // 无论支付·结果 都跳转历史交易页 // 跳转自己不会刷新 直接获取新数据
        setTimeout(() => {this.gettable()}, 500);
      }else{
        // 支付宝支付
        console.log("支付宝支付")
        // 不拦截支付
        sessionStorage.setItem('StopRedirectPay',"false")
        setTimeout(() => this.$router.push(`/alipay_account?id=${this.alipay.id}&money=${this.alipay.money}&num=${this.alipay.num}&name=${this.alipay.name}`), 1000);
      }
      
      this.dialogVisible = false
    },
    async getuserinfo(){
    await axios.get('/user/info')
    .then(response=>{
      //if(response.data.code)this.$message.success("获取成功")
      //else this.$message.error("获取失败："+response.data.msg)
      this.obj = response.data.data
      if(this.obj.addresses===null) this.obj.addresses=[]
      else{
        // 默认地址置顶
        for(var i=0;i<this.obj.addresses.length;i++){
          if(this.obj.addresses[i].is_default){
            const temp = this.obj.addresses[0]
            this.obj.addresses[0] = this.obj.addresses[i]
            this.obj.addresses[i] = temp
          }
        }
      }
      return this.obj
    }).catch(error=>{
      console.log(error)
      this.$message.error("获取失败："+error.data.msg)
      return this.obj
    })
    },
    // 余额支付
    async moneypay(order_id){
      console.log("余额支付")
      var result = false
      await axios.post(`/order/payonmoney?order_id=${order_id}`)
      .then(response=>{
        if(response.data.code){
          this.$message.success(response.data.data)
          //setTimeout(() => {window.location.reload()}, 500);
          result =  true
        }
        else {
          this.$message.error(response.data.msg)
          console.log(response)
          result =  false
        }
      }).catch(error=>{
        this.$message.error(error.data.msg);
        console.log(error)
        result =  false
      })
      return result
    }
  },
  created(){
    // 没数据才请求
    if(!this.PtableData)this.gettable()
    else {
      this.IsTableLoading = false
      this.tableData=  this.PtableData
    }
    this.getuserinfo()
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
