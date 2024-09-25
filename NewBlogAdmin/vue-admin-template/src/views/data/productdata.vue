<template>
  <div class="app-container">
    <div style="margin: 0 0 20px 0">
      <el-button type="primary" plain @click="OpenDialog('新增商品')">新增</el-button>
      <el-button type="primary" plain @click="OpenDialog('删除商品')">删除</el-button>
      <el-button type="primary" plain @click="OpenDialog('修改商品')">修改</el-button>
      <el-button type="primary" plain @click="OpenDialog('查询商品')">查询</el-button>
      
      <!-- 排序方式 -->
      <div style="margin: 8px 0 -5px 5px;padding: 0;">
        <el-dropdown trigger="click" @command="column=>{value2 = column.value,value2_text=column.text}">
          <span class="el-dropdown-link" style="cursor: pointer;color: gray;">
            {{value2_text}}<i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown" :style="'margin-left: '+this.$store.state.app.scrollbar_width+'px;'">
            <el-dropdown-item v-for="(column, index) in this.option2"
            :key="index" @click="testinfo(column.text)"
            :command=column :style="value2 === column.value?'color:red;':''" >{{column.text}}<i :style="value2 === column.value?'margin-left: 10px;':'display:none;'" class="el-icon-check"></i></el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>

      <el-tag
        style="margin:10px 10px 0 10px;padding-left: 10px;"
        :disable-transitions=true
        v-for="(tag,key) in tags"
        :key="key"
        closable
        @close="removeTag(key)"
        :type="tag.type">
        {{tag.name}}
      </el-tag>
    </div>

    <!-- 标题检索 -->
    <div v-if="biao==='t_product'" style="margin: -15px 5px 10px;">
        <span style="color:#808080;margin-right: 10px;"> 标题检索</span>
        <input
            style="width: 85%;margin: 5px auto;height: 30px;width:200px;"
            class="el-input__inner"
            v-model="biaotijiansuo"
            @keyup.enter="dialogdata3.name=biaotijiansuo,gotosearch()"
            enterkeyhint="send"
            placeholder=""
            type="text"
        />
    </div>

    <el-table
    ref="multipleTable"
      v-loading="IsTableLoading"
      :data="tableData"
      border fit highlight-current-row
      @select="handleCheckBoxSelect"
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
            <template slot="header" >
                <myfilter 
                :column="column" 
                :biao="biao"
                :fenlei_map="dataResult.fenlei_map"
                :biaoqian_map="dataResult.biaoqian_map"
                :fliter_list="fliter_list"
                :ref="column.prop"

                :yaunsuListMap="yaunsuListMap"
                :currentPage="currentPage"
                :PageSize="PageSize"
                value2="value2"
                :dialogdata3="dialogdata3"
                @selectpagebyadminWithSearch_SetList="selectpagebyadminWithSearch_SetList"/>
            </template>

          <template slot-scope="scope" >
            <div v-if="column.label==='创建时间' || column.label==='更新时间' || column.label==='最后访问时间'">{{ tableData[scope.$index][column.prop]?tableData[scope.$index][column.prop].replace(/T/g, ' '):null }}</div>
            <div v-else-if="column.label==='图像'||column.label==='略缩图'">
              <a target="_blank" :href="`${staticPath}${tableData[scope.$index][column.prop]}.webp`">
                <img v-if="tableData[scope.$index][column.prop] && tableData[scope.$index][column.prop]!=='noproduct'" loading="lazy"  :src="`${staticPath}${tableData[scope.$index][column.prop]}.webp`" style="height:80px;width:80px;object-fit: cover;">
              </a>
              </div>
              <div v-else-if="column.label==='url预览' || column.label==='图片url'">
                <el-image v-if="tableData[scope.$index][column.prop]" style="cursor: pointer;"
                    class="img" 
                    lazy
                    :preview-src-list="[tableData[scope.$index][column.prop]]"
                    :title=tableData[scope.$index][column.prop] :src=tableData[scope.$index][column.prop] />
              </div>
              <div v-else-if="column.label==='微信头像'"><img style="border-radius: 30px;" v-lazy width="80px" :src=tableData[scope.$index][column.prop]></div>
              <div v-else-if="column.label==='点赞数'">
                {{ tableData[scope.$index][column.prop]?.length}}
              </div>
              <div v-else-if="column.label==='分类'">
                {{ tableData[scope.$index][column.prop]?dataResult?.fenlei_map?.[tableData[scope.$index][column.prop]]:"none"}}
              </div>
              <div v-else-if="column.label==='标签'">
                {{ tableData[scope.$index][column.prop]?translate_biaoqianList(tableData[scope.$index][column.prop]):"none"}}
              </div>
            <div v-else>
              {{ tableData[scope.$index][column.prop]}}
            </div>
          </template>
      </el-table-column>

      <el-table-column
        fixed="right"
        label="操作"
        width="120">
        <template slot-scope="scope">
            <el-button type="primary" size="mini" plain @click="del(scope.row)">删</el-button>
            <el-button type="primary" size="mini" plain @click="upd(scope.row)">改</el-button>
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

    <!-- 弹出框1 (增改) -->
    <el-dialog :fullscreen="true" :title="this.dialog_title2" :visible.sync="dialogVisible2" width="90%" height="90%" 
    >
      
      <el-form  style="margin:0 20px 0 0;" :rules="rules" label-position="right" ref="form" :model="dialogdata2" label-width="80px">
        <!-- 使用el-row和el-col创建栅格布局 -->
        <el-row  v-if="biao==='t_product'" :gutter="20">

          <el-col :span="['置顶', '显示', '主页显示'].some(str => column.label.includes(str))?3:12" 
            v-for="(column, index) in columns" :key="index">
            <!-- 不隐藏的要在这注明 -->
            <span v-if="column.label!=='标题' && column.label!=='描述信息'&& column.label!=='分类' && column.label!=='图像url'&& column.label!=='标签'
            && ['置顶', '显示', '主页显示'].every(str => !column.label?.includes(str))" />
            
            <el-form-item v-else-if="column.label==='分类'" :label="column.label" :prop="column.prop">
                
                <el-radio-group v-if="dataResult" v-model="dataResult.fenlei_map[dialogdata2[column.prop]]">R
                    <el-radio-button 
                        v-for="(item, index) in dataResult?.fenlei_map" :key="index" 
                        @click.native="dialogdata2[column.prop]=index"
                        :label="item">
                    </el-radio-button>   
                </el-radio-group>
                <span style="margin-left: 20px;">{{  dialogdata2[column.prop] }}</span>

            </el-form-item>

            <el-form-item v-else-if="column.label==='标签'" :label="column.label" :prop="column.prop">
                <el-select
                    style="width: 100%;height: 40px;max-width: 300px;"
                    v-model="selectedOptions"
                    multiple
                    filterable
                    allow-create
                    default-first-option
                    placeholder="请选择文章标签">
                    <el-option
                    v-for="(item,key) in dataResult.biaoqian_map"
                    :key="key"
                    :label="item"
                    :value="item">
                    </el-option>
                </el-select>
            </el-form-item>



            <el-form-item v-else-if="['置顶', '显示', '主页显示'].some(str => column.label.includes(str))" :label="column.label" :prop="column.prop">
                
                <el-switch
                v-model="dialogdata2[column.prop]"
                active-color="#13ce66"
                inactive-color="#ff4949">
                </el-switch>

            </el-form-item>

            
            <el-form-item v-else :label="column.label" :prop="column.prop">
              <el-input v-model="dialogdata2[column.prop]" style="max-width: 300px;">
              </el-input>
            </el-form-item>

          </el-col>

          

          <el-col :span="24">
            <el-form-item label="封面图像" prop="photo" >
              <el-upload
                style="margin: 5px 0 0 5px;"
                class="upload-demo"
                ref="upload"
                :show-file-list="true"
                :file-list="fileList"
                action="#"
                :limit="1"
                
                :before-upload="beforeUpload"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :on-exceed="handleExceed"
                :http-request="handleFileUpload"
                list-type="picture">
                <el-button size="small" type="primary">点击上传</el-button>
                <div slot="tip" class="el-upload__tip">只能上传jpg/png/webp一张，且不超过10mb</div>
              </el-upload>
              <p>图像url优先显示，如果有</p>
            </el-form-item>
          </el-col>
          <el-col v-show="false" :span="24">
            <el-form-item label="略缩图" prop="photo_shot" >
              <el-upload
                style="margin: 5px 0 0 5px;"
                class="upload-demo"
                ref="upload2"
                :show-file-list="true"
                :file-list="fileList2"
                action="#"
                :limit="1"
                
                :before-upload="beforeUpload"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :on-exceed="handleExceed"
                :http-request="handleFileUpload"
                list-type="picture">
                <el-button size="small" type="primary">点击上传</el-button>
                <div slot="tip" class="el-upload__tip">只能上传jpg/png一张，且不超过500kb</div>
              </el-upload>
              <p>暂时没开发略缩图功能</p>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="内容" prop="content" >
                <mavon-editor :toolbars="markdownOption"
                ref="editor" @imgAdd="$imgAdd" @imgDel="$imgDel"  v-model="dialogdata2.content"
                :xssOptions="xssOptions">
                    <!-- 左工具栏后加入自定义按钮  -->
                    <template slot="left-toolbar-after">
                        <button
                            type="button"
                            @click="show_emojiP = !show_emojiP"
                            aria-hidden="true"
                            title="emoji表情"
                            class="but"
                            :style="`${show_emojiP?'background-color:#eaeaea;':''}`"
                        >emoji
                        </button>
                        <button
                            type="button"
                            @click="confirm_change()"
                            aria-hidden="true"
                            title="发送文章"
                            class="but"
                        >提交
                        </button>
                        <button
                            type="button"
                            @click="confirm_change(true)"
                            aria-hidden="true"
                            title="发送文章"
                            class="but"
                        >保存
                        </button>

                        <Draggable>
                            <EmojiPicker v-show="show_emojiP" @emoji-selected="emoji_selected" class="emoji"/>
                        </Draggable>

                    </template>
                </mavon-editor>
              <el-button type="primary" plain style="margin-top: 20px;" @click="confirm_change()">send</el-button>
            </el-form-item>
          </el-col>

        </el-row>

        <el-row  v-else :gutter="20">
            <el-col  v-for="(column, index) in columns" :key="index">
                <!--修改框忽略-->
                <span v-if="['点赞数'].some(str => column.label?.includes(str))" />

                <el-form-item v-else :label="column.label" :prop="column.prop">
                    <el-input v-model="dialogdata2[column.prop]" style="max-width: 300px;"></el-input>
                </el-form-item>
            </el-col>
            <el-button style="float:right;margin-right: 10%;" @click="confirm_change()">提交</el-button>
        </el-row>
        
      </el-form>

    </el-dialog>

    <!-- 弹出框1-2(地址选择) -->
    <El2 ref="address" style="text-align:left;margin-left:20px;margin-bottom:20px;" @address="address_finish" />


    <!-- 弹出框2（查询用户） -->
    <el-dialog :title="this.dialog_title3" :visible.sync="dialogVisible3" width="60%" >
  
      <el-form  style="margin:0 20px 0 0;" label-position="right" ref="form2" :model="dialogdata3" label-width="80px">
        <!-- 使用el-row和el-col创建栅格布局 -->
        <el-form-item v-for="(column, index) in columns2" :key="index" :label="column.label" :prop="column.prop">
          <div style="display: flex;" v-if="column.label==='时间'">
            <el-date-picker
              v-model="dialogdata3.startDate"
              value-format="yyyy-MM-dd HH:mm:ss"
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
import El2 from '/src/components/AddressAdd/ElAddress2'
import EmojiPicker from './emoji/EmojiPicker.vue'
import Draggable from './draggable/index'
import myfilter from './filter'


//import isEqual from 'lodash/isEqual';

export default {
    props:{
        biao: {
            type: String,
            default:'t_product'
        },
        columns: {
            type: Array,
            default() {
                return [];
            }
        },
        fliter_list:{
            type:Array,
            default() {
                return ['置顶', '显示', '主页显示', '分类','标签'];
            }
        }
    },
    components:{
        myfilter,
        El2,
        EmojiPicker,
        Draggable,

    },
    data() {
        return {
            biaotijiansuo:null,
            selectedOptions:[],
            tagList:[],
            toggleRowSelection:null,
            //biao:'t_product',

            xssOptions: {
                whiteList: {
                    svg: ["class", "viewBox", "version", "xmlns", "width", "height", "t", "p-id"],
                    path: ["d", "fill", "p-id"],
                    a: ["href", "title", "target", "download","id","style"],
                    img: ["src", "alt", "width", "height"],
                    video: ["src", "type", "controls", "width", "height", "poster"],
                    source: ["src", "type"],
                    br: [],
                    span:["style"],
                    div: ["class","style","id","onclick"],
                    iframe: ["style","src", "scrolling", "border", "frameborder", "framespacing", "allowfullscreen","width", "height"]
                },
                stripIgnoreTagBody: true
            },
            
            radio1:null,
            dataResult:null,
            show_emojiP:false,
            markdownOption: {
                bold: true, // 粗体
                italic: true, // 斜体
                header: true, // 标题
                underline: true, // 下划线
                strikethrough: true, // 中划线
                mark: true, // 标记
                superscript: true, // 上角标
                subscript: true, // 下角标
                quote: true, // 引用
                ol: true, // 有序列表
                ul: true, // 无序列表
                link: true, // 链接
                imagelink: true, // 图片链接
                code: true, // code
                table: true, // 表格
                fullscreen: true, // 全屏编辑
                readmodel: true, // 沉浸式阅读
                htmlcode: true, // 展示html源码
                help: true, // 帮助
                /* 1.3.5 */
                undo: true, // 上一步
                redo: true, // 下一步
                trash: true, // 清空
                //save: true, // 保存（触发events中的save事件）
                /* 1.4.2 */
                navigation: true, // 导航目录
                /* 2.1.8 */
                alignleft: true, // 左对齐
                aligncenter: true, // 居中
                alignright: true, // 右对齐
                /* 2.2.1 */
                subfield: true, // 单双栏模式
                preview: true, // 预览
            },
            // region vant主页筛选商品/排序
            value2: 'b',
            value2_text:'时间排序',
            option2: [
                { text: '默认排序', value: 'a' },
                { text: '时间排序', value: 'b' },
                //{ text: '访问量排序', value: 'c' },
                //{ text: '销量排序', value: 'd' },
                //{ text: '评分排序', value: 'e' },
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
            fileList2: [
            ],
            //
            tableData: [],
            currentPage:1,
            PageSize:10,
            TotalPage:null,
            IsTableLoading:true,
            
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
            "content":null,
            "is_top":false,
            "is_show":true,
            "is_on_homepage":true,
        },
            radio:null,
            addresses:[],
            rules: {
                name: [
                    { required: true, message: '请输入标题', trigger: 'blur' }
                ],
                type: [
                    { required: true, message: '请选择分类', trigger: 'blur' }
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
            //"type2":null,
        },
            columns2 : [
                { prop: 'id', label: 'ID', width: '180' },
                { prop: 'name', label: '姓名', width: '80' },
                //{ prop: 'type2', label: '同类名', width: '80' },
                { prop: 'create_time', label: '时间', width: '80' },
            ],
            tags: [
            ],
            yaunsuListMap:{}
        }
    },
    methods:{
        translate_biaoqianList(list){
            var trans = []
            list.forEach(key=>{
                trans.push(this.dataResult.biaoqian_map[key])
            })
            return trans
        },
        handleBiaoqianTagClose(index,list){
            this.$delete(list,index)
        },
        formatDateWithTimezone(dateStr) {
            if(dateStr===null)return null
            const date = new Date(dateStr);
            // 加上8个小时的毫秒数
            date.setHours(date.getHours() + 8);
            return date.toISOString()  // 输出新的时间字符串
        },
        del(row){
            this.rows_selection = [row]
            this.$confirm('确定删除  '+(row?.name?row.name:'这个')+'  吗?', '提示', {
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
                this.rows_selection = []        
            });
        },
        // 更新前 前端序列化true false和其他
        upd(row){
            console.log('row')
            console.log(row)
            // 前端序列化true false
            Object.keys(row).forEach(key => {
                row[key] = row[key] === 0 && key.startsWith('is')?false:
                    row[key] === 1 && key.startsWith('is')?true : row[key];
            });
            console.log('row2')
            console.log(row)
            // 初始化数据
            //this.dialogdata2 =  JSON.parse(JSON.stringify(row))
            // 更新响应式
            this.$set(this, 'dialogdata2', JSON.parse(JSON.stringify(row)))
            console.log('this.dialogdata2')
            console.log(this.dialogdata2)

            // 初始化验证 // ?.防止this.$refs.form为空报错 为空不运行
            this.$refs.form?.resetFields();
            // 初始化标题
            this.dialog_title2 = '修改'
            // 初始化图片
            this.init_dialogdata2_photo()
            // 初始化分类
            this.radio1 = this.dataResult.fenlei_map[this.dialogdata2.type]
            // 初始化标签
            if(this.biao==="t_product"){
                var biaoqian_trans_list = [] 
                row.t_tag_label?.forEach(num=>biaoqian_trans_list.push(this.dataResult.biaoqian_map[num]))
                this.selectedOptions = biaoqian_trans_list
            }

            this.dialogVisible2 = true

            //初始化富文本
            this.$nextTick(() => {
                this.$refs.editor.content = this.dialogdata2.content
            })

        },
        // 点击勾选框时
        handleCheckBoxSelect(selection, row){
            console.log(selection)
            console.log(row)
        },
        selectpagebyadminWithSearch_SetList(add,del,biao){
            console.log('add:')
            console.log(add)
            console.log('del:')
            console.log(del)
            for (let [key, list] of Object.entries(add)) {
                if(this.yaunsuListMap[key])this.yaunsuListMap[key]=this.yaunsuListMap[key].concat(list)
                else this.yaunsuListMap[key] = list
            }
            for (let [key, list] of Object.entries(del)) {
                list.forEach(obj=>{
                    const index = this.yaunsuListMap[key].findIndex(item => JSON.stringify(item) === JSON.stringify(obj))
                    console.log(index)
                    if(index!==-1)this.yaunsuListMap[key].splice(index,1)
                })
            }
            //this.init_Tags()

            console.log('sum:')
            console.log(this.yaunsuListMap)
            console.log('biao:')
            console.log(biao)

            this.getbyyaunsuListMap(biao)
        },
        getbyyaunsuListMap(biao){
            this.IsTableLoading = true
            this.dialogdata3.endDate = this.formatDateWithTimezone(this.dialogdata3.endDate)
            this.dialogdata3.startDate = this.formatDateWithTimezone(this.dialogdata3.startDate)
            this.init_Tags()

            axios.post('/all/selectpagebyadminWithSearch_SetList',{
                'yaunsuListMap':this.yaunsuListMap,
                'biao':biao,
                'currentPage': this.currentPage, 
                'PageSize': this.PageSize,
                'value2':this.value2,
                ...this.dialogdata3,
                'tagList':this.tagList
            })
                .then(response=>{
                    if(response.data.code===0)this.$message.error(response.data.msg)
                    else {
                        console.log(response.data.data)
                        this.tableData = response.data.data.records
                        this.TotalPage = response.data.data.total
                        this.currentPage = response.data.data.current
                        if(this.columns.length===0)
                            this.init_columns_by_sql(response.data.data.records)
                        this.IsTableLoading = false
                    }
                })
        },
        init_columns_by_sql(records){
            if(this.biao==='t_product'){
                this.columns = [
                    { prop: 'id', label: 'ID', width: '80' },
                    { prop: 'name', label: '标题', width: '180' },
                    { prop: 'type', label: '分类' , width: '80',align:'center' },
                    { prop: 't_tag_label', label: '标签' , width: '100',align:'center' },
                    { prop: 'user_name', label: '作者' , width: '80',align:'center' },
                    //{ prop: 'type2', label: '分类2' , width: '80' ,align:'center'},
                    { prop: 'info', label: '描述信息' },
                    //{ prop: 'content', label: '内容', width: '80' },
                    //{ prop: 'price', label: '价格', width: '80' },
                    //{ prop: 'num', label: '数量', width: '80' },
                    //{ prop: 'sold_num', label: '销量', width: '80' },
                    { prop: 'visited_num', label: '浏览量', width: '80' },
                    //{ prop: 'love_list', label: '点赞数' , width: '80' ,align:'center'},
                    //{ prop: 'rate', label: '评分', width: '80' },
                    //{ prop: 'rate_num', label: '评分人数', width: '80' },
                    { prop: 'photo', label: '图像', width: '100',align:'center' },
                    { prop: 'photo_url', label: '图像url', width: '100',align:'center' },
                    { prop: 'photo_url', label: 'url预览', width: '100',align:'center' },
                    //{ prop: 'photo_shot', label: '略缩图', width: '100',align:'center' },
                    
                    { prop: 'is_top', label: '置顶', width: '80' },
                    { prop: 'is_show', label: '显示', width: '80' },
                    { prop: 'is_on_homepage', label: '主页显示', width: '100' },
                    //{ prop: 'photo_list', label: '图片列表' , width: '80' ,align:'center'},
                    { prop: 'create_time', label: '创建时间', width: '180' },
                    { prop: 'update_time', label: '更新时间', width: '180' },
                    //{ prop: 'version', label: '版本', width: '100' },
                ]
            }else{
                this.columns = [{ prop: 'id', label: 'ID', width: '80' }]
                if(records.length===0)return
                Object.keys(records[0]).forEach(key=>{
                    if(key!=='id'&&key!=='create_time')this.columns.push({ prop: key, label: key },)
                })
                this.columns.push({ prop: 'create_time', label: '创建时间', width: '180' })
            }
        },
        getall(){
            axios.get('/data-result/all')
                .then(response=>{
                    console.log(response.data)
                    //this.dataResult = response.data
                    //this.dataResult = { ...response.data };
                    this.dataResult = JSON.parse(JSON.stringify(response.data));

                })
        },
        emoji_selected(emoji){
            console.log(this.$refs.editor.$refs)
            //console.log(this.$refs.editor.$refs.doms)
            const editor = this.$refs.editor.$refs.vNoteTextarea.$refs.vTextarea;

            // 保存当前的光标位置
            const cursorPos = editor.selectionStart;
            console.log
            const textBeforeCursor = this.dialogdata2.content.substring(0, cursorPos);
            const textAfterCursor = this.dialogdata2.content.substring(cursorPos);

            this.dialogdata2.content = textBeforeCursor + emoji + textAfterCursor;

            // 将光标位置移动到插入内容之后
            this.$nextTick(() => {
                editor.selectionStart = cursorPos + emoji.length;
                editor.selectionEnd = cursorPos + emoji.length;
                editor.focus();
            });
            
        },
        $imgDel(){
            this.$message("不想开发这个功能，去他的（我打算用正则在提交后，把正文有的图片从temp移到文章的文件夹，然后清空temp文件夹完事）")

        },
        // 绑定@imgAdd event
        $imgAdd(pos, $file){
            // 第一步.将图片上传到服务器.
            var formdata = new FormData();
            formdata.append('photo', $file);
            axios({
                url: '/product/saveTempPhotobyadmin',
                method: 'post',
                data: formdata,
                headers: { 'Content-Type': 'multipart/form-data' },
            }).then(res => {
                // 第二步.将返回的url替换到文本原位置![...](0) -> ![...](url)
                /**
                 * $vm 指为mavonEditor实例，可以通过如下两种方式获取
                 * 1. 通过引入对象获取: `import {mavonEditor} from ...` 等方式引入后，`$vm`为`mavonEditor`
                 * 2. 通过$refs获取: html声明ref : `<mavon-editor ref=md ></mavon-editor>，`$vm`为 `this.$refs.md`
                 */
                if(res.data.code)
                    this.$refs.editor.$img2Url(pos, this.staticPath + res.data.data);
                else
                    this.$message.error(res.data.data)
            })
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
            if(this.dialogdata2.photo_shot!==null && this.dialogdata2.photo_shot!=="noproduct")this.fileList2 = [{ name: `${this.dialogdata2?.photo_shot}.webp`, url: `${this.staticPath}${this.dialogdata2.photo_shot}.webp` }];
        },
        // 这里上传完图片和beforeUpload一起触发 // 这里上传完才会初始化this.$refs.upload.uploadFiles?.[0]?.raw
        handleFileUpload(file) {
            console.log("submit")
            //console.log(file)
            this.photo = file
            //console.log(this.photo.file)
            console.log(this.$refs.upload.uploadFiles?.[0]?.raw)
        },
        // 图片上传前检查（:auto-upload="false"会使得:before-upload="beforeUpload"被禁止）
        beforeUpload(file) {
            console.log("beforeUpload!")
            const allowedTypes = ['image/jpeg', 'image/png'];
            const isJPGorPNG = allowedTypes.includes(file.type);
            const isLt500KB = file.size / 1024 < 5000;

            if (!isJPGorPNG) {
                this.$message.error('只能上传jpg/png文件!(我不管，webp也要行)');
                return true;
                //return false;
            }
            if (!isLt500KB) {
                this.$message.error('上传文件大小不能超过 2000KB!');
                return false;
            }
            return true;
        },
        // 图片移除（数组）
        handleRemove(file, fileList) {
            console.log("remove")
            console.log(file, fileList);
            console.log(this.$refs.upload?.uploadFiles?.[0]?.raw)
            this.dialogdata2.photo = "noproduct"
        },
        // 图片预览（数组）
        handlePreview(file) {
            console.log("preview")
            console.log(file);
        },
        // 图片超出限制
        handleExceed() {
            this.$message.error('只能上传一张图片');
        },
        // 地址组件选完 触发的事件
        address_finish(index,val){
            console.log("父组件")
            console.log(index)
            console.log(val)

            if(this.$refs.address.dialog_title==='新增地址'){
                const address = JSON.parse(JSON.stringify(val))

                // 默认
                if(address?.is_default){
                    this.addresses.forEach(item=>{
                        item.is_default = false
                    })
                }
                this.addresses.push(address)
            }

            else if(this.$refs.address.dialog_title==='更改地址'){
                const address = JSON.parse(JSON.stringify(val))
                console.log(address)
                // 默认
                if(address?.is_default){
                    this.addresses.forEach(item=>{
                        item.is_default = false
                    })
                }

                //this.addresses[index] = JSON.parse(JSON.stringify(val)) 直接改索引 vue不会渲染
                this.$set(this.addresses, index, address);
            }
        
        
            else{
                this.$message.error("走到这一步，说明你写错了")
            }
            console.log(this.addresses)
        

        },
        // 删除地址（数组里某个）
        delete_address(index2){
            console.log(index2)
            this.addresses.splice(index2,1)
        },
        // 新加地址
        new_address(){
            this.$refs.address.dialogdata =
        {
            "info": [],
            "info_code":[],
            "name": "",
            "phone": "",
            "detail": "",
            "is_default":false
        },
            this.$refs.address.dialog_title='新增地址'
            this.$refs.address.dialogVisible=true;
        },
        // 更改地址（数组里某个）// 这里深拷贝
        change_address(index2,data2){
            this.$refs.address.dialogdata = JSON.parse(JSON.stringify(data2))
            this.$refs.address.dialog_title = '更改地址'
            this.$refs.address.dialogindex = index2
            this.$refs.address.dialogVisible=true
        },
        gettable(){
            this.getbyyaunsuListMap(this.biao)
            /*
            this.IsTableLoading = true
            axios.get('/product/page',{
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
                    //this.$message.success("获取成功")
                }
            }).catch(error=>{
                this.$message.error(error.data.msg)
                console.log(error)
            })
                */
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

            if(val==="新增商品"){

                // 初始化数据
                this.dialogdata2 = 
                {
                    //'type':null
                }
          
                // 初始化验证 // ?.防止this.$refs.form为空报错 为空不运行
                this.$refs.form?.resetFields();

                this.dialog_title2 = "新增"
                this.dialogVisible2 = true

                //初始化富文本
                this.$nextTick(() => {
                    this.$refs.editor.content = this.dialogdata2.content
                })

                // 初始化标签
                this.selectedOptions = []
            }

            else if(val==="删除商品"){
                if(this.rows_selection.length===0){
                    this.$message.error("请选择要删除的")
                    return
                }

                this.$confirm('确定删除这'+(this.rows_selection.length)+'个吗?', '提示', {
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
                    this.$message.error("请选择要修改的")
                    return
                }

                if(this.rows_selection.length!==1){
                    this.$message.error("一次只能修改一个")
                    return
                }

                this.upd(this.rows_selection?.[0])

            }

            else if(val==="查询商品"){
                this.dialogVisible3 = true
            }

            else{
                //
            }

        
        },
        // 展开框1的确定
        confirm_change(is_baocun){

            this.$refs.form.validate(result => {
                if (result) {
                    console.log("验证通过");
                    if(this.dialog_title2==="新增")
                        this.axios_add()
                    else if(this.dialog_title2==="修改")
                        this.axios_update(is_baocun)
                    else
                        this.$message.error("error")
                } else{
                    console.log("验证失败");
                }
            })

        },
        // iframe 视频插入高宽
        insert_sth_to_content(iframeHtml){
            // 防空
            if(!iframeHtml)return ""

            // b站视频扩长
            //if(!iframeHtml.includes('width="100%" height="600"')){
            iframeHtml = iframeHtml.replace(
                /<iframe([^>]*allowfullscreen="true")>/,
                '<iframe$1 width="100%" height="600">'
            );
            //}

            // B站视频 https 高清 danmaku关 autoplay=0
            //if(!iframeHtml.includes('&high_quality=1')){
            iframeHtml = iframeHtml.replace(
                /src="(\/\/player.bilibili.com\/player.html\?.*?&p=\d+)"/g,
                'src="https://$1&high_quality=1&danmaku=1&autoplay=0"')
            //}


            return iframeHtml
        },
        // 添加一个
        axios_add(){
            if(this.biao!=='t_product'){
                Object.keys(this.dialogdata2).forEach(key => {
                    if(this.dialogdata2[key]==="true")this.dialogdata2[key] = true
                    if(this.dialogdata2[key]==="false")this.dialogdata2[key] = false
                });
                return axios.post(`/all/insertonebyadmin/${this.biao}`,this.dialogdata2).then(response=>{
                    if(response.data.code===0)this.$message.error(response.data.msg)
                    else{
                        this.dialogVisible2 = false
                        this.$message.success("增加成功")
                        this.gettablebycondition()
                    }
                })
            }

            //this.$refs.upload.submit();
            console.log(this.$refs.upload);
            console.log(this.dialogdata2)
            console.log(this.$refs.upload.uploadFiles?.[0]?.raw)

            // iframe 视频插入高宽
            this.dialogdata2.content = this.insert_sth_to_content(this.dialogdata2.content)

            let formData = new FormData()
            formData.append('photo', this.$refs.upload.uploadFiles?.[0]?.raw)
            formData.append('photo_shot', this.$refs.upload2.uploadFiles?.[0]?.raw)
            formData.append('product_json',JSON.stringify(this.dialogdata2)) // 序列化
            formData.append('tagList_json',JSON.stringify(this.tagList))

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
                        this.getall() // 有时添加了新标签 要重新获取
                    }
                }).catch(error=>{
                    this.$message.error(error.data.msg)
                    console.log(error)
                })
        },
        // 更新一个
        // 新范例 防404报错
        axios_update(is_baocun){
            // 非t_product的入口
            if(this.biao!=='t_product'){
                Object.keys(this.dialogdata2).forEach(key => {
                    if(this.dialogdata2[key]==="true")this.dialogdata2[key] = true
                    if(this.dialogdata2[key]==="false")this.dialogdata2[key] = false
                });
                return axios.put(`/all/updateonebyadmin/${this.biao}`,this.dialogdata2).then(response=>{
                    if(response.data.code===0)this.$message.error(response.data.msg)
                    else{
                        this.dialogVisible2 = false
                        this.$message.success("更新成功")
                        this.gettablebycondition()
                    }
                })
            }
            
            console.log(this.dialogdata2)
            console.log(this.$refs.upload)
            let formData = new FormData()

            // iframe 视频插入高宽
            this.dialogdata2.content = this.insert_sth_to_content(this.dialogdata2.content)

            // photo只有上传了新图片才会有
            formData.append('photo', this.$refs.upload.uploadFiles?.[0]?.raw)
            formData.append('photo_shot', this.$refs.upload2.uploadFiles?.[0]?.raw)
            formData.append('product_json',JSON.stringify(this.dialogdata2)) // 序列化
            formData.append('tagList',JSON.stringify(this.tagList))

            axios.put('/product/updateonebyadmin',formData,{
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            })
                .then(response=>{
                    if(response.data.code===0)this.$message.error(response.data.msg)
                    else {
                        if(is_baocun){
                            this.$message.success("保存成功")
                            this.gettablebycondition()
                            this.getall() // 有时添加了新标签 要重新获取
                        }else{
                            this.dialogVisible2 = false
                            this.$message.success("更新成功")
                            this.gettablebycondition()
                            this.getall() // 有时添加了新标签 要重新获取
                        }
                        
                    }
                }).catch(error=>{
                    if(error.data?.msg)this.$message.error(error.data.msg)
                    else this.$message.error(error)
                    console.log(error)
                })
        },
        // 删一堆
        delete_byadmin(){
            var Ids = []
            this.rows_selection.forEach(obj=>{
                if(obj.id)Ids.push(obj.id)
            })
            axios.delete(`/all/deleteIdsbyadmin/${this.biao}`, {data:Ids})
                .then(response=>{
                    if(response.data.code===0)this.$message.error(response.data.msg)
                    else {
                        this.dialogVisible2 = false
                        this.$message.success("删除成功:"+response.data.data)
                        this.gettablebycondition()
                    }
                }).catch(error=>{
                    if(error.data?.msg)this.$message.error(error.data.msg)
                    else this.$message.error(error)
                    console.log(error)
                })
        },
        // 发起axios条件查询
        gettablebycondition(){
            this.getbyyaunsuListMap(this.biao)
            /*
            this.IsTableLoading = true
            var page = {
                currentPage: this.currentPage,
                PageSize: this.PageSize
            }
            var params = {...page,...this.dialogdata3,'value2':this.value2}
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
            
                    // #region 获取筛选
                    var init_dialogdata3 = {};
                    for (let [key, value] of Object.entries(response.data.map)) {
                        if(key!=="currentPage" && key!=="PageSize" && key!=="value2"){
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
            */
        },
        // 初始化Tag
        init_Tags(){
            var listMap = this.yaunsuListMap
            var tags = []
            // fliter的tag
            for (let [key, list] of Object.entries(listMap)) {
                if(list.length){
                    list.forEach(item=>{
                        if(key==='type'){
                            tags.push({ name: `${key} : ${this.dataResult.fenlei_map[item.value]}`, 
                                prop:key, item:item, 
                                type: 'success'})
                        }else{
                            tags.push({ name: `${key} : ${item.value}`, 
                                prop:key, item:item, 
                                type: 'success'})
                        }
                    })
                }
            }
            // dialogdata3的tag
            const obj = this.dialogdata3
            Object.keys(obj).forEach(key => {
                if(key==='startDate'||key==='endDate'){
                    const date = new Date(obj[key]);  // 假设 obj[key] 是一个可以被解析为日期的值
                    const formattedDate = date.toISOString().substring(0, 19);
                    if(formattedDate.includes('1970-01-01T00:00:00')){
                        obj[key]=null
                    }else{
                        obj[key]=formattedDate
                    }
                    
                }
                
                obj[key] !== null && obj[key] !== ""?
                    tags.push({ name: `${key} : ${obj[key]}`, prop:key, item:obj, type: 'success',isdialogdata3:true,dialogdata3_key:key}) 
                    : "";
            });




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
        
            //this.init_Tags()

            // 关闭窗口
            this.dialogVisible3 = false

            // #region 发起条件查询
            this.gettablebycondition()
        },
        // 点击Tag的删除
        removeTag(index){
            // 移除Tag
            console.log(index)
            // 单独处理dialogdata3的tag(如果是dialogdata3的tag)
            if(this.tags[index]?.isdialogdata3){
                this.dialogdata3[this.tags[index].dialogdata3_key]=null
                this.$delete(this.tags, index)
                this.gettablebycondition()  
            }
            
            const obj = this.tags[index]
            const index2 = this.yaunsuListMap[obj.prop].findIndex(item => JSON.stringify(item) === JSON.stringify(obj.item))
            this.$delete(this.yaunsuListMap[obj.prop], index2)// 本地仓移除

            console.log(this.$refs[obj.prop])
            var typelist = this.$refs[obj.prop][1].search_add_last_time[obj.prop]
            if(!typelist)typelist = this.$refs[obj.prop][0].search_add_last_time[obj.prop]
            //console.log(this.$refs[obj.prop][0].search_add_last_time[obj.prop])
            //console.log(this.$refs[obj.prop][1].search_add_last_time[obj.prop])
            //console.log(obj.prop)
            const index3 = typelist.findIndex(item => JSON.stringify(item) === JSON.stringify(obj.item))
            this.$delete(typelist, index3)// 子组件仓移除 // 移除是移除了 但没更新
            this.$root.$emit('update_'+obj.prop) // 强制更新子组件

            this.$delete(this.tags, index)
            // 同时发起新条件查询
            this.gettablebycondition()      
        },
        deletetempfolder(){
            axios({
                url: '/product/deleteTempPhotobyadmin',
                method: 'delete',
            })
        }
    },
    mounted(){
        this.gettablebycondition()
        this.deletetempfolder()
        this.getall()
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
            //this.gettablebycondition()
            this.selectpagebyadminWithSearch_SetList({},{},this.biao)
        },
        selectedOptions:function(){
            var biaoqian_list = []
            // 是不是新的标签
            this.selectedOptions.forEach(str=>{
                var exist = false
                for (let [key, value] of Object.entries(this.dataResult.biaoqian_map)) {
                    // 旧标签
                    if(value===str){
                        biaoqian_list.push(key)
                        exist = true
                        break
                    }
                }
                // 新标签
                if(!exist)biaoqian_list.push(str)
            })
            console.log(biaoqian_list)
            this.dialogdata2.t_tag_label = biaoqian_list
        }
    }
}
</script>

<style scoped>

.addtag{
    background-color: white;
}
.addtag:hover{
    background-color: rgb(245, 242, 242);
}
.input-new-tag {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
  }
.radio-group-content {
  white-space: normal; /* 允许内容换行 */
  word-break: break-word; /* 在长单词或URL地址内部进行换行 */
}
.but{
    background-color:transparent;
    border-color: transparent;
    cursor: pointer;
    font-weight: 300;
    border-radius: 5px;
}
.but:hover{
    background-color: #eaeaea;
}
.emoji{
    width:400px;
    height: 160px;
    border-radius: 20px;
    margin: 5px;
    padding: 10px;
    overflow-y: scroll;
}
.paiming{
    cursor: pointer;
    position: relative
}
.paimingbox{
    z-index: 99999;
    position: absolute;
    width:100px;
    height: 200px;
    background-color: aquamarine;
}

</style>
