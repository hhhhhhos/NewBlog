<template>
  <div class="app-container">
    <div style="margin: 0 0 20px 0;">
      <el-button type="primary" plain @click="OpenDialog(`新增`)">新增</el-button>
      <el-button type="primary" plain @click="OpenDialog(`删除`)">删除</el-button>
      <el-button type="primary" plain @click="OpenDialog(`修改`)">修改</el-button>
      <el-button type="primary" plain @click="OpenDialog(`查询`)">查询</el-button>
      <el-tag
        style="margin:10px 10px 0 20px;padding-left: 10px;"
        :disable-transitions=true
        v-for="tag in tags"
        :key="tag.name"
        closable
        @close="removeTag(tag.key)"
        :type="tag.type">
        {{tag.name}}
      </el-tag>
    </div>

    <el-table
      v-loading="IsTableLoading"
      :data="tableData"
      border fit highlight-current-row
      @selection-change="handleSelectionChange"
      style="width:100%;">

      <el-table-column
        type="selection"
        align="center"
        width="40">
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
            <div v-if="column.label==='创建时间'">{{ tableData[scope.$index][column.prop].replace(/T/g, ' ') }}</div>
            <div v-else>
              {{ tableData[scope.$index][column.prop]}}
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
          :total="TotalPage"
          style="margin-top: 20px;">
    </el-pagination>

    <!-- 弹出框1 -->
    <el-dialog :title="this.dialog_title2" :visible.sync="dialogVisible2" width="60%"  >
      
      <el-form  style="margin:0 20px 0 0;" :rules="rules" label-position="right" ref="form" :model="dialogdata2" label-width="30px">
        <!-- 使用el-row和el-col创建栅格布局 -->
        <el-row :gutter="20">
          <el-col :span="24" >

            
            <el-form-item label="" >
              <pt @select_row="select_row" ></pt>
            </el-form-item>

            <el-form-item >
              <pt2 ref="pt2"></pt2>
            </el-form-item>
          
          </el-col>
        </el-row>

        
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible2 = false,gettable()">取消</el-button>
        <el-button type="primary" @click="confirm_change">确定</el-button>
      </span>
    </el-dialog>


    <!-- 弹出框2（查询用户） -->
    <el-dialog :title="this.dialog_title3" :visible.sync="dialogVisible3" width="60%" >
  
      <el-form  style="margin:0 20px 0 0;" label-position="right" ref="form2" :model="dialogdata3" label-width="80px">
        <!-- 使用el-row和el-col创建栅格布局 -->
        <el-form-item v-for="(column, index) in columns2" :key="index" :label="column.label" :prop="column.prop">
          <div style="display: flex;" v-if="column.label==='时间'">
            <el-date-picker
              v-model="dialogdata3.startDate"
              type="datetime"
              placeholder="选择开始时间">
            </el-date-picker>
            <el-date-picker
              v-model="dialogdata3.endDate"
              type="datetime"
              placeholder="选择结束时间">
            </el-date-picker>
          </div>
          <el-input style="max-width: 250px;" v-else v-model="dialogdata3[column.prop]"></el-input>
        </el-form-item>         
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible3 = false">取消</el-button>
        <el-button type="primary" @click="gotosearch()">确定</el-button>
      </span>

    </el-dialog>

  </div>
</template>

<script>
import axios from '@/utils/axios';
import pt from './components/productdata';
import pt2 from './components/productdata2';


  export default {
    components:{
      pt,
      pt2
    },
    data() {
      return {
        Replace_text:"关联",
        Replace_url:"product-related-list",
        tableData: [],
        tableData2: [], // 终仓
        currentPage:1,
        PageSize:10,
        TotalPage:null,
        IsTableLoading:true,
        columns : [
        { prop: 'id', label: 'ID', width: '180' },
        { prop: 'create_time', label: '创建时间', width: '180' },
        { prop: 'product_id_list', label: '关联产品ID表',  },
        ],
        dialogVisible2:false,
        dialog_title2:"",
        dialogdata2:
        {
          "id": null,
          "name":null,
          "age": null,
          "sex": null,
          "phone": null,
          "addresses":null,
          "role":null,
          "password":null,
          "money":null,
          "create_time":null,
          "version":null,
        },
        radio:null,
        addresses:[],
        rules: {
          name: [
            { required: true, message: '请输入姓名', trigger: 'blur' },
            //{ min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
          ],
          password: [
            { required: true, message: '请输入密码', trigger: 'blur' },
            //{ min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
          ],
        },
        rows_selection:[],
        // 弹窗2（查询弹窗）
        dialogVisible3:false,
        dialog_title3:"查询筛选添加",
        dialogdata3:
        {
          "id": null,
          "name":null,
          "startDate":null,
          "endDate":null,
        },
        columns2 : [
          { prop: 'id', label: 'ID', width: '180' },
          { prop: 'name', label: '姓名', width: '80' },
          { prop: 'create_time', label: '时间', width: '80' },
        ],
        tags: [
        ]
      }
    },
    methods:{
      select_row(val){
        console.log(val)
        // 去重合并
        this.$refs.pt2.tableData =  [...new Set([...this.$refs.pt2.tableData , ...val])];
      },
      // 页容量变化
      handleSizeChange(val) {
        this.PageSize = val
        this.gettablebycondition()
      },
      // 切页
      handleCurrentChange(val) {
        this.currentPage = val
        this.gettablebycondition()
      },
      // 表格列选择
      handleSelectionChange(val){
        console.log(val)
        this.rows_selection = val
      },
      // 点增删改查
      OpenDialog(val){

        if(val==="新增"){
          // 初始化数据
          this.dialogdata2 = 
          {
            "id": null,
            "product_id_list":null,
            "create_time":null,
          },
          this.addresses = []
          // 初始化验证 // ?.防止this.$refs.form为空报错 为空不运行
          this.$refs.form?.resetFields();

          this.dialog_title2 = val
          this.dialogVisible2 = true
        }

        else if(val==="删除"){
          if(this.rows_selection.length===0){
            this.$message.error("请选择要删除的用户")
            return
          }

          this.$confirm('确定删除这'+(this.rows_selection.length)+'个用户吗?', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              this.delete_byadmin() // 删除用户
            }).catch(() => {
              this.$message({
                type: 'info',
                message: '已取消删除'
              });          
            });
        }

        else if(val==="修改"){
          if(this.rows_selection.length===0){
            this.$message.error("请选择要修改的用户")
            return
          }

          if(this.rows_selection.length!==1){
            this.$message.error("一次只能修改一个用户")
            return
          }


          // 初始化数据
          this.dialogdata2 = this.rows_selection?.[0]
          this.addresses = this.rows_selection?.[0]?.addresses
          // 初始化验证 // ?.防止this.$refs.form为空报错 为空不运行
          this.$refs.form?.resetFields();

          this.dialog_title2 = val
          this.dialogVisible2 = true
        }

        else if(val==="查询"){
          this.dialogVisible3 = true
        }

        else{
          //
        }

        
      },
      // 展开框1的确定
      confirm_change(){
        if(this.dialog_title2==="新增")
          this.axios_add()
        else if(this.dialog_title2==="修改")
          this.axios_update()
        else
          this.$message.error("error")

      },
      // 添加一个
      axios_add(){
        axios.post('/product-related-list/addonebyadmin',
          this.$refs.pt2.tableData
        )
        .then(response=>{
          if(response.data.code===0)this.$message.error(response.data.msg)
          else {
            this.dialogVisible2 = false
            this.$message.success("添加成功")
            this.gettablebycondition()
          }
        }).catch(error=>{
          this.$message.error(error.data.msg)
          console.log(error)
        })
      },
      // 更新一个
      // 新范例 防404报错
      axios_update(){
        console.log(this.dialogdata2)
        axios.put('/user/updateonebyadmin',this.dialogdata2)
        .then(response=>{
          if(response.data.code===0)this.$message.error(response.data.msg)
          else {
            this.dialogVisible2 = false
            this.$message.success("更新成功")
            this.gettablebycondition()
          }
        }).catch(error=>{
          if(error.data?.msg)this.$message.error(error.data.msg)
          else this.$message.error("error")
          console.log(error)
        })
      },
      // 删一堆
      delete_byadmin(){
        axios.post('/user/deletelistbyadmin',this.rows_selection)
        .then(response=>{
          if(response.data.code===0)this.$message.error(response.data.msg)
          else {
            this.dialogVisible2 = false
            this.$message.success("删除成功")
            this.gettablebycondition()
          }
        }).catch(error=>{
          this.$message.error(error.data.msg)
          console.log(error)
        })
      },
      // 发起axios条件查询
      gettablebycondition(){
        this.IsTableLoading = true
        var page = {
            currentPage: this.currentPage,
            PageSize: this.PageSize
          }
        var params = {...page,...this.dialogdata3}
        
        axios.get(`/${this.Replace_url}/selectpagebyadmin`,{
          params: params
        }).then(response=>{
          if(response.data.code===0)this.$message.error(response.data.msg)
          else {
            this.tableData = response.data.data.records
            this.TotalPage = response.data.data.total
            this.IsTableLoading = false
            //this.$message.success("获取成功")
            
            // #region 获取筛选
            var init_dialogdata3 = {};
            for (let [key, value] of Object.entries(response.data.map)) {
              if(key!=="currentPage" && key!=="PageSize"){
                init_dialogdata3[key] = value
              }
            }
            this.dialogdata3 = init_dialogdata3
            // #endregion
            
            // 初始化Tag
            this.init_Tags()
          }
        }).catch(error=>{
          this.$message.error(error.data.msg)
          console.log(error)
        })
      },
      // 初始化Tag
      init_Tags(){
        var tags = []
        console.log(this.dialogdata3)
        for (let [key, value] of Object.entries(this.dialogdata3)) {
          console.log(key + ': ' + value);
          if(value!==null){
            var key_name = key
            if(key==='name')key_name = "姓名"
            if(key==='startDate')key_name = "开始日期"
            if(key==='endDate')key_name = "结束日期"

            tags.push({ name: `${key_name} : ${value}`, type: 'success' , key:key})
          }
        }
        this.tags = tags
      },
      // 点击查询框确定
      gotosearch(){
        var is_allnull = true
        for (let value of Object.values(this.dialogdata3)) {
          if(value!==null){
            is_allnull = false
            break
          }
        }
        if(is_allnull){
          this.$message.error("无筛选参数")
          return
        }
        
        // 初始化Tag
        this.init_Tags()

        // 关闭窗口
        this.dialogVisible3 = false

        // #region 发起条件查询
        this.gettablebycondition()
      },
      // 点击Tag的删除
      removeTag(key){
        // 移除Tag
        console.log(key)
        this.tags = this.tags.filter(tag => tag.key !== key);

        // #region 同时删除查询对象dialogdata3的对应值
        var dialogdata3 =
        {
          "id": null,
          "name":null,
          "startDate":null,
          "endDate":null,
        }
        for (let [key2, value] of Object.entries(this.dialogdata3)) {
          if(key===key2)continue
          dialogdata3[key2] = value
        }
        this.dialogdata3 = dialogdata3

        console.log(this.dialogdata3)
        // #endregion
      

        // 同时发起新条件查询
        this.gettablebycondition()      
      },
      isValidUUID(uuid) {
          const regex = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
          return regex.test(uuid);
      }
    },
    mounted(){
      this.gettablebycondition()
    }
  }
</script>

<style scoped>
.radio-group-content {
  white-space: normal; /* 允许内容换行 */
  word-break: break-word; /* 在长单词或URL地址内部进行换行 */
}
</style>
