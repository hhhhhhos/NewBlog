<template>
  <div>
    <vue-editor
      id="editor"
      useCustomImageHandler
      @image-added="handleImageAdded"
      v-model="content" style="background-color: white;"
    >
    </vue-editor>
    <el-button @click="send">send</el-button>

    <div class="quillWrapper" style="background-color: white;">
      <div class="ql-container ql-snow">
        <div v-html="content2" class="ql-editor" data-gramm="false" contenteditable="false">
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import { VueEditor } from "vue2-editor";
import axios from '@/utils'

export default {
  components: {
    VueEditor
  },

  data() {
    return {
      content: "<h1>Some initial content</h1>",
      content2:""
    };
  },

  methods:{
    send(){
      axios.post('/test/fuck', this.content, {
        headers: {
          'Content-Type': 'application/json'
        }
      })
      .then(response=>{
        this.content2 = response.data
      }).catch(error=>{
        this.$message.error(error.data.msg);
        console.log(error)
      })
    },
    handleImageAdded(file, Editor, cursorLocation, resetUploader) {
      // An example of using FormData
      // NOTE: Your key could be different such as:
      // formData.append('file', file)

      var formData = new FormData();
      formData.append("image", file);

      axios({
        url: "https://fakeapi.yoursite.com/images",
        method: "POST",
        data: formData
      })
        .then(result => {
          const url = result.data.url; // Get url from response
          Editor.insertEmbed(cursorLocation, "image", url);
          resetUploader();
        })
        .catch(err => {
          console.log(err);
        });
    }
  }
};
</script>