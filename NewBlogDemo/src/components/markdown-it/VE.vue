<template>
      <div v-html="renderedMarkdown"></div>
</template>

<script>
import MarkdownIt from 'markdown-it';

export default {
    name: 'MarkdownRenderer',
    props: {
        content: {
            type: String,
            required: true,
        },
    },
    data() {
        return {
            md: new MarkdownIt(),
        };
    },
    computed: {
        renderedMarkdown() {
            return this.md.render(this.content);
        },
    },
};
</script>

<style scoped>
/* 你可以在这里添加一些基本的样式 */
</style>
