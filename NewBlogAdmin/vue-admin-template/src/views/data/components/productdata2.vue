<template>
  <div>
    
    <el-table
      v-loading="IsTableLoading"
      :data="tableData"
      border fit highlight-current-row
      @selection-change="handleSelectionChange"
      style="overflow: scroll;">


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
            <div v-else-if="column.label===''">
              <i class="el-icon-delete" style="cursor: pointer;color:orange;" @click="delete_row(scope.$index)"></i>
            </div>
            <div v-else-if="column.label==='图像'">
              <a target="_blank" :href="`${staticPath}${tableData[scope.$index][column.prop]}.webp`">
                <img  loading="lazy"  :src="`${staticPath}${tableData[scope.$index][column.prop]}.webp`" style="height:80px;width:80px;object-fit: cover;">
              </a>
              </div>
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

  </div>
</template>

<script>
import axios from '@/utils/axios';



  export default {
    components:{
      
    },
    props:{

    },
    data() {
      return {
        fliter_name:null,
        // region vant主页筛选商品/排序
        value2: 'a',
        value2_text:'默认排序',
        option2: [
        { text: '默认排序', value: 'a' },
        { text: '时间排序', value: 'b' },
        { text: '访问量排序', value: 'c' },
        { text: '销量排序', value: 'd' },
        { text: '评分排序', value: 'e' },
      ],
        dropdown_isclick:false,
        //endregion
        // 表格图片获取路径
        staticPath: process.env.VUE_APP_STATIC_PATH,
        // 图片
        photo:null,
        // 图片(el有bug绑定不上)
        fileList: [
        ],
        //
        tableData: [],
        currentPage:1,
        PageSize:10,
        TotalPage:null,
        IsTableLoading:false,
        columns : [
        {  label: '' ,width: '40'},
        { prop: 'id', label: 'ID' ,width: '120'},
        { prop: 'product_related_list_id', label: '关联表ID'  },
        { prop: 'name', label: '姓名', width: '120' },
        { prop: 'type', label: '商品分类' , width: '80' },
        ],
        dialogVisible2:false,
        dialog_title2:"",
        dialogdata2:
        {
          "id": null,
          "name":null,
          "price": null,
          "num": null,
          "photo": null,
          "info":null,
          "type":null,
          "create_time":null,
          "version":null,
        },
        radio:null,
        addresses:[],
        rules: {
          name: [
            { required: true, message: '请输入商品名', trigger: 'blur' },
            { 
              validator: (rule, value, callback) => {
                // 验证是否只包含数字
                if (!value) {
                  callback(new Error('请输入'));
                } else if (value < 0 || value >= 7) {
                  // 验证数字是否大于0且小于1000000
                  callback(new Error('商品名必须大于0且小于7位数'));
                } else {
                  // 如果验证通过
                  callback();
                }
              }, 
              trigger: 'blur'
            }
            //{ min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
          ],
          price: [
            { required: true, message: '请输入价格', trigger: 'blur' },
            //{ min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
            { 
              validator: (rule, value, callback) => {
                // 验证是否只包含数字
                if (!value) {
                  callback(new Error('请输入数字值'));
                } else if (!/^\d+(\.\d+)?$/.test(value)) {
                  // 正则表达式 ^\d+$ 用于检查字符串是否只包含数字
                  callback(new Error('只能输入数字'));
                } else if (value < 0 || value >= 1000000) {
                  // 验证数字是否大于0且小于1000000
                  callback(new Error('数字必须大于等于0且小于7位数'));
                } else {
                  // 如果验证通过
                  callback();
                }
              }, 
              trigger: 'blur'
            }
          ],
          num: [
            { required: true, message: '请输入数量', trigger: 'blur' },
            //{ min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
            { 
              validator: (rule, value, callback) => {
                // 验证是否只包含数字
                if (!value) {
                  callback(new Error('请输入数字值'));
                } else if (!/^\d+$/.test(value)) {
                  // 正则表达式 ^\d+$ 用于检查字符串是否只包含数字
                  callback(new Error('只能输入数字'));
                } else if (value < 0 || value >= 1000000) {
                  // 验证数字是否大于0且小于1000000
                  callback(new Error('数字必须大于等于0且小于7位数'));
                } else {
                  // 如果验证通过
                  callback();
                }
              }, 
              trigger: 'blur'
            }
          ],
        },
        rows_selection:[],
        // 弹窗2（查询弹窗）
        dialogVisible3:false,
        dialog_title3:"查询筛选添加",
        dialogdata3:
        {
          //"id": null,
          "name":null,
          //"startDate":null,
          //"endDate":null,
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
      delete_row(val){
        console.log("删除"+val)
        this.tableData.splice(val,1)
      },
      input_event(val){
        console.log(val)
        this.gettablebycondition(val)
      },
      testinfo(info){
        console.log(info)
      },
      // 修改窗口打开时，初始化图片fileList到web显示（此时this.$refs.upload.uploadFiles?.[0]?.raw为空）
      init_dialogdata2_photo(){
        console.log("this.$refs.upload.uploadFiles?.[0]?.raw")
        console.log(this.$refs.upload?.uploadFiles?.[0]?.raw)
        console.log(this.dialogdata2)
        if(this.dialogdata2.photo!=="noproduct")this.fileList = [{ name: `${this.dialogdata2?.photo}.webp`, url: `${this.staticPath}${this.dialogdata2.photo}.webp` }];
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
        this.$emit("select_row",val)
      },
      // 点增删改查
      OpenDialog(val){

        if(val==="新增商品"){

          // 初始化数据
          this.dialogdata2 = 
          {
            "id": null,
            "name":null,
            "price": null,
            "num": null,
            "photo": null,
            "info":null,
            "type":null,
            "create_time":null,
            "version":null,
          }
          // 初始化验证 // ?.防止this.$refs.form为空报错 为空不运行
          this.$refs.form?.resetFields();

          this.dialog_title2 = val
          this.dialogVisible2 = true
        }

        else if(val==="删除商品"){
          if(this.rows_selection.length===0){
            this.$message.error("请选择要删除的商品")
            return
          }

          this.$confirm('确定删除这'+(this.rows_selection.length)+'个商品吗?', '提示', {
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

        else if(val==="修改商品"){
          if(this.rows_selection.length===0){
            this.$message.error("请选择要修改的商品")
            return
          }

          if(this.rows_selection.length!==1){
            this.$message.error("一次只能修改一个商品")
            return
          }

          // 初始化数据
          this.dialogdata2 = this.rows_selection?.[0]
          // 初始化验证 // ?.防止this.$refs.form为空报错 为空不运行
          this.$refs.form?.resetFields();
          // 初始化标题
          this.dialog_title2 = val
          // 初始化图片
          this.init_dialogdata2_photo()

          this.dialogVisible2 = true

        }

        else if(val==="查询商品"){
          this.dialogVisible3 = true
        }

        else{
          //
        }

        
      },
      // 展开框1的确定
      confirm_change(){

        this.$refs.form.validate(result => {
                if (result) {
                  console.log("验证通过");
                  if(this.dialog_title2==="新增商品")
                    this.axios_add()
                  else if(this.dialog_title2==="修改商品")
                    this.axios_update()
                  else
                    this.$message.error("error")
                } else{
                  console.log("验证失败");
                }
            })

      },
      // 添加一个
      axios_add(){
        //this.$refs.upload.submit();
        console.log(this.$refs.upload);
        console.log(this.dialogdata2)
        console.log(this.$refs.upload.uploadFiles?.[0]?.raw)
        let formData = new FormData()
        formData.append('photo', this.$refs.upload.uploadFiles?.[0]?.raw)
        formData.append('product_json',JSON.stringify(this.dialogdata2)) // 序列化
        axios.post('/product/addonebyadmin',formData,{
          headers: {
              'Content-Type': 'multipart/form-data'
          }
        })
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
        console.log(this.$refs.upload)
        let formData = new FormData()
        // photo只有上传了新图片才会有
        formData.append('photo', this.$refs.upload.uploadFiles?.[0]?.raw)
        formData.append('product_json',JSON.stringify(this.dialogdata2)) // 序列化
        axios.put('/product/updateonebyadmin',formData,{
          headers: {
              'Content-Type': 'multipart/form-data'
          }
        })
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
        axios.post('/product/deletelistbyadmin',this.rows_selection)
        .then(response=>{
          if(response.data.code===0)this.$message.error(response.data.msg)
          else {
            this.dialogVisible2 = false
            this.$message.success("删除成功")
            this.gettablebycondition()
          }
        }).catch(error=>{
          if(error.data?.msg)this.$message.error(error.data.msg)
          else this.$message.error("error")
          console.log(error)
        })
      },
      // 发起axios条件查询
      gettablebycondition(val){
        this.dialogdata3.name = val
        this.IsTableLoading = true
        var page = {
            currentPage: this.currentPage,
            PageSize: this.PageSize
          }
        var params = {...page,...this.dialogdata3}
        console.log(params)
        
        axios.get('/product/selectpagebyadmin',{
          params: params
        }).then(response=>{
          if(response.data.code===0)this.$message.error(response.data.msg)
          else {
            this.tableData = response.data.data.records
            this.TotalPage = response.data.data.total
            this.IsTableLoading = false
            //this.$message.success("获取成功")
            

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
      }
    },
    watch:{
      dialogVisible2:function(){
        if(!this.dialogVisible2){
          if(this.$refs.upload){
            this.$refs.upload.uploadFiles.splice(0)
          }
        }
      },
      value2:function(){
        this.gettablebycondition()
      }
    }
  }
</script>

<style scoped>
.radio-group-content {
  white-space: normal; /* 允许内容换行 */
  word-break: break-word; /* 在长单词或URL地址内部进行换行 */
}
</style>
