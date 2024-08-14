<template>
  <div class="dashboard-container">
    <div class="dashboard-text">Role: {{ this.name }}</div>
    
    <panel-group @handleSetLineChartData="handleSetLineChartData" />
    
    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px;">
      <line-chart :chart-data="lineChartData"  @PointClick="PointClick"/>
    </el-row>

    <el-row >
      <el-col :span="24">
        <!-- 访客表 -->
        <el-table
          v-show="type==='newVisitis'"
          v-loading="tableloading"
          :data="tableData"
          height="350"
          style="width: 100%">
          <el-table-column
            prop="user_uuid"
            label="UUID"
            width="310">
          </el-table-column>
          <el-table-column
            label="城市名"
            width="180">
            <template slot-scope="scope" >
              <span v-html="repla(scope.row.city)"></span>
            </template>
          </el-table-column>
          <el-table-column
            label="用户名"
            width="180">
            <template slot-scope="scope" >
              <span v-html="repla(scope.row.visitor_names)"></span>
            </template>
          </el-table-column>
          <el-table-column
            label="微信名"
            width="100"
            align="center">
            <template slot-scope="scope" >
              <span v-html="repla(scope.row.wechat_nickname)"></span>
            </template>
          </el-table-column>
          <el-table-column
            label="头像"
            width="100"
            align="center">
            <template slot-scope="scope" >
              <img style="width:40px;height:40px;border-radius: 40px;" v-if="scope.row.wechat_headimgurl" :src="scope.row.wechat_headimgurl">
            </template>
          </el-table-column>
          <el-table-column
            prop="deviceName"
            label="设备"
            width="310">
          </el-table-column>
          <!-- 别删这行 是为了撑起来美观-->
          <el-table-column
            prop="eeee"
            label=""
            >
          </el-table-column>
          <el-table-column
            label="当前"
            width="180"
            align="center">
            <template slot-scope="scope" >
              <el-tag v-if="isWithinOneMinute(scope.row.max_create_time)" type="success">在线
              </el-tag>
              <el-tag v-else >离线
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="min_create_time"
            label="最早访问时间"
            width="180">
          </el-table-column>
          <el-table-column
            prop="max_create_time"
            label="最后访问时间"
            width="180">
          </el-table-column>
          <el-table-column
            prop="total_count"
            label="访问次数"
            width="100"
            align="center">
          </el-table-column>
        </el-table>

        <!-- 订单表 -->
        <el-table
          v-show="type==='purchases'"
          :row-key="generateUUID()"
          v-loading="tableloading"
          :data="tableData"
          height="350"
          style="width: 100%">
          <el-table-column
            prop="id"
            label="订单ID"
            width="200">
          </el-table-column>
          <el-table-column
            prop="user_id"
            label="用户ID"
            width="200">
          </el-table-column>
          <el-table-column
            prop="user_info.name"
            label="用户名"
            width="200">
          </el-table-column>
          <el-table-column
            prop="user_info.wechat_nickname"
            label="微信名"
            width="200">
          </el-table-column>
          <!-- 别删这行 是为了撑起来美观-->
          <el-table-column
            prop="eeee"
            label=""
            >
          </el-table-column>
          <el-table-column
            prop="totalMoney"
            label="金额"
            width="180"
            align="center">
          </el-table-column>
          <el-table-column
            label="创建时间"
            width="180">
            <template slot-scope="scope" >
              {{  myreplace(scope.row.create_time)}}
            </template>
          </el-table-column>
          <el-table-column
            label="支付状态"
            width="100"
            align="center">
            <template slot-scope="scope">
                <el-tag v-if="scope.row.status==='未支付'" >{{ scope.row.status }}
                </el-tag>
                <el-tag v-else type="success" >{{ scope.row.status }}
                </el-tag>
            </template>
          </el-table-column>
        </el-table>

        <!-- 购物车表 -->
        <el-table
          v-show="type==='shoppings'"
          :row-key="generateUUID()"
          v-loading="tableloading"
          :data="tableData"
          height="350"
          style="width: 100%">
          <el-table-column
            prop="id"
            label="购物ID"
            width="200">
          </el-table-column>
          <el-table-column
            prop="user_id"
            label="用户ID"
            width="200">
          </el-table-column>
          <el-table-column
            prop="product_id"
            label="商品ID"
            width="200">
          </el-table-column>
          <el-table-column
            prop="wechat_nickname"
            label=""
            >
          </el-table-column>
          <el-table-column
            prop="product_num"
            label="数量"
            width="180"
            align="center">
          </el-table-column>
          <el-table-column
            label="创建时间"
            width="180">
            <template slot-scope="scope" >
              {{  myreplace(scope.row.create_time) }}
            </template>
          </el-table-column>

        </el-table>

        <el-pagination
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-size=5
          background
          layout="prev, pager, next"
          :total="TotalPage"
          style="margin-top: 20px;">
        </el-pagination>

      </el-col>
    </el-row>

    

    <Echart1 :x_data="Echart1.x_data"
        :y_data="Echart1.y_data"
        :color="Echart1.color"
        :title="Echart1.title" 
        style="display: none;"/>

  </div>
</template>

<script>
import PanelGroup from './components/PanelGroup'
import LineChart from './components/LineChart'
import Echart1 from './components/Echart1'
import axios from '@/utils/axios';

let lineChartData = {
    newVisitis: {
        actualData: [120, 82, 91, 154, 162, 140, 145],
        name:"访客数量",
        color:"#40c9c6"
    },
    messages: {
        name:"留言数量",
        actualData: [180, 160, 151, 106, 145, 150, 130],
        color:"#36a3f7"
    },
    purchases: {
        name:"订单数量",
        actualData: [120, 90, 100, 138, 142, 130, 130],
        color:"#f4516c"
    },
    shoppings: {
        name:"购物数量",
        actualData: [120, 82, 91, 154, 162, 140, 130],
        color:"#34bfa3"
    }
}

export default {
    name: 'Dashboard',
    components: {
        PanelGroup,
        Echart1,
        LineChart
    },
    data(){
        return{
            name:"unknown",
            Echart1:{
                x_data: null,
                y_data: null,
                color: null,
                title:null
            },
            lineChartData: lineChartData.newVisitis,
            response_data:null,
            tableData:[],
            val:null,
            currentPage:1,
            TotalPage:1,
            tableloading:true,
            type:"newVisitis"
        }
    },
    computed: {

    },
    methods: {
        myreplace(val) {
            if (val) {
                return String(val).replace("T", " ");
            }
            return "";
        },
        generateUUID() {
            return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
                (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
            );
        },
        handleSetLineChartData(type) {
            this.lineChartData = lineChartData[type]
            console.log(type)
            if(type === "messages")return this.$message("留言模块开发中")
            this.type = type
            this.PointClick(6)
            this.getVistorsNum()
        },
        getrole(){
            axios.get('/user/session')
                .then(response=>{
                    console.log(response.data)
                    this.name = response.data?.[0].slice(6)
                }).catch(error=>{
                    this.$message.error("error")
                    console.log(error)
                })
            console.log(name)
        },
        async getVistorsNum(){
            await axios.get(`/user-agent-details/select_dashboard_${this.type}`)
                .then(response=>{
                    console.log(response.data)
                    this.response_data = response.data
                    //var x_data = []
                    var y_data = []
                    response.data.forEach(element => {
                        //x_data.push(element.create_time.slice(6))
                        if (element.user_uuids) {  // 检查 element 是否有 user_uuids 属性
                            y_data.push(element.user_uuids.length)
                        } else if (element.count !== undefined) {  // 如果没有 user_uuids，检查是否有 count 属性
                            y_data.push(element.count)
                        } else {
                            y_data.push(0)  // 如果两者都不存在，推入默认值0
                        }
                    });
                    //this.Echart1.x_data = x_data
                    //this.Echart1.y_data = y_data
                    //this.Echart1.color = 'rgba(10, 87, 196, 0.545)'
                    //this.Echart1.title = "访客记录"

                    lineChartData[this.type].actualData = y_data
                }).catch(error=>{
                    console.log(error)
                })
        },
        async init_dashboard_echarts_data(){
            this.getrole()
            await this.getVistorsNum()
            setTimeout(() => {
                this.PointClick(6)
            }, 200)
      
        },
        PointClick(val){
            this.tableloading = true
            this.val = val
            axios.post(`/user-agent-details/select_dashboard_${this.type}/click`,{
                create_time:this?.response_data[val]?.create_time,
                currentPage:this.currentPage
            }).then(response=>{
                this.tableData = response.data.records
                this.TotalPage = response.data.total
                this.currentPage = response.data.current
                console.log(response)
            }).catch(error=>{
                console.log(error)
            }).finally(() => {
                // 这里放置无论成功与否都需要执行的代码
                this.tableloading = false;
            });
        },
        // 切页
        handleCurrentChange(val) {
            this.currentPage = val
            this.PointClick(this.val)
        },
        repla(val) {
            if (val == null || typeof val === 'undefined') {
                return ''; // 或者任何你认为合适的默认值
            }
            return val.replace(/,/g, "<br>"); // 替换所有逗号为 <br> 标签
        },
        isWithinOneMinute(maxCreateTime) {
            const oneMinute = 60 * 1000; // 一分钟的毫秒数
            const maxTime = new Date(maxCreateTime); // 假设 maxCreateTime 是一个 ISO 格式的日期字符串
            const now = new Date();
            const diff = now - maxTime; // 时间差（毫秒）
            return diff <= oneMinute;
        }
    },
    mounted(){
        this.init_dashboard_echarts_data()
    }
}
</script>

<style lang="scss" scoped>
.dashboard {
  &-container {
    margin: 30px;
  }
  &-text {
    font-size: 30px;
    line-height: 46px;
  }
}
</style>
