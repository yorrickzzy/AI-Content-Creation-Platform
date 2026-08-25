<script setup>
import {
    Edit,
    Delete
} from '@element-plus/icons-vue'

import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

//文章分类数据模型
const categorys = ref([
    {
        "id": 3,
        "categoryName": "美食",
        "categoryAlias": "my",
        "createTime": "2023-09-02 12:06:59",
        "updateTime": "2023-09-02 12:06:59"
    },
    {
        "id": 4,
        "categoryName": "娱乐",
        "categoryAlias": "yl",
        "createTime": "2023-09-02 12:08:16",
        "updateTime": "2023-09-02 12:08:16"
    },
    {
        "id": 5,
        "categoryName": "军事",
        "categoryAlias": "js",
        "createTime": "2023-09-02 12:08:33",
        "updateTime": "2023-09-02 12:08:33"
    }
])

//用户搜索时选中的分类id
const categoryId=ref('')

//用户搜索时选中的发布状态
const state=ref('')

const stateOptions = [
    { label: '已发布', value: '已发布' },
    { label: '草稿', value: '草稿' }
]

//文章列表数据模型
const articles = ref([
    {
        "id": 5,
        "title": "陕西旅游攻略",
        "content": "兵马俑,华清池,法门寺,华山...爱去哪去哪...",
        "coverImg": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/9bf1cf5b-1420-4c1b-91ad-e0f4631cbed4.png",
        "state": "草稿",
        "categoryId": 2,
        "createTime": "2023-09-03 11:55:30",
        "updateTime": "2023-09-03 11:55:30"
    },
    {
        "id": 5,
        "title": "陕西旅游攻略",
        "content": "兵马俑,华清池,法门寺,华山...爱去哪去哪...",
        "coverImg": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/9bf1cf5b-1420-4c1b-91ad-e0f4631cbed4.png",
        "state": "草稿",
        "categoryId": 2,
        "createTime": "2023-09-03 11:55:30",
        "updateTime": "2023-09-03 11:55:30"
    },
    {
        "id": 5,
        "title": "陕西旅游攻略",
        "content": "兵马俑,华清池,法门寺,华山...爱去哪去哪...",
        "coverImg": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/9bf1cf5b-1420-4c1b-91ad-e0f4631cbed4.png",
        "state": "草稿",
        "categoryId": 2,
        "createTime": "2023-09-03 11:55:30",
        "updateTime": "2023-09-03 11:55:30"
    },
])

//分页条数据模型
const pageNum = ref(1)//当前页
const total = ref(20)//总条数
const pageSize = ref(3)//每页条数

//当每页条数发生了变化，调用此函数
const onSizeChange = (size) => {
    pageSize.value = size
    articleList()
}
//当前页码发生变化，调用此函数
const onCurrentChange = (num) => {
    pageNum.value = num
    articleList()
}

//回显文章分类
import {
    articleCategoryListService,
    articleListService,
    articleAddService,
    articleDetailService,
    articleUpdateService,
    articleDeleteService,
    articleSummaryService,
    articleAssistantService
} from '@/api/article.js'
const articleCategoryList= async()=>{
    let result=await articleCategoryListService();
    categorys.value=result.data;
}

//获取文章列表数据
const articleList=async()=>{
    let params={
        pageNum:pageNum.value,
        pageSize:pageSize.value,
        categoryId:categoryId.value ? categoryId.value:null,
        state:state.value ? state.value:null
    }
    let result=await articleListService(params);

    //渲染视图
    total.value=result.data.total;
    articles.value=result.data.items;

    //处理数据，给数据模型扩展一个属性categoryName,分类名称
    for (let i=0;i<articles.value.length;i++){
        let article=articles.value[i];
        article.categoryName = '未分类'
        for (let j=0;j<categorys.value.length;j++){
            if (article.categoryId==categorys.value[j].id){
                article.categoryName=categorys.value[j].categoryName;
            }
        }
    }
}

const resetSearch = () => {
    categoryId.value = ''
    state.value = ''
    pageNum.value = 1
    articleList()
}

const initPage = async () => {
    await articleCategoryList()
    await articleList()
}

onMounted(() => {
    initPage()
})

import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import {Plus} from '@element-plus/icons-vue'
//控制抽屉是否显示
const visibleDrawer = ref(false)
const drawerTitle = ref('添加文章')
const editorContent = ref('')
const editorKey = ref(0)

const clearArticleModel = () => {
    articleModel.value = {
        id: '',
        title: '',
        categoryId: '',
        summary: '',
        coverImg: '',
        content: '',
        state: ''
    }
    editorContent.value = ''
    editorKey.value++
    assistantSuggestions.value = null
}

const showAddDrawer = () => {
    drawerTitle.value = '添加文章'
    visibleDrawer.value = true
    clearArticleModel()
}
//添加表单数据模型
const articleModel = ref({
    id: '',
    title: '',
    categoryId: '',
    summary: '',
    coverImg: '',
    content:'',
    state:''
})

//导入token
import { useTokenStore } from '@/stores/token.js';
const tokenStore=useTokenStore();
const uploadProgress = ref(0)
const uploadingCover = ref(false)

//上传成功的回调函数
const uploadSuccess=(result)=>{
    uploadingCover.value = false
    uploadProgress.value = 0
    if (result.code !== 0 || !result.data) {
        ElMessage.error(result.message || result.msg || '封面上传失败')
        return
    }
    articleModel.value.coverImg=result.data
    ElMessage.success('封面上传成功')
}

const beforeCoverUpload = (file) => {
    if (!file.type.startsWith('image/')) {
        ElMessage.warning('请上传图片文件')
        return false
    }
    if (file.size > 5 * 1024 * 1024) {
        ElMessage.warning('封面图片不能超过 5MB')
        return false
    }
    uploadingCover.value = true
    uploadProgress.value = 0
    return true
}

const coverUploadProgress = (event) => {
    uploadProgress.value = Math.round(event.percent || 0)
}

const coverUploadError = () => {
    uploadingCover.value = false
    uploadProgress.value = 0
    ElMessage.error('封面上传失败，请检查网络、登录状态或 OSS 配置后重试')
}

const syncEditorContent = (content) => {
    editorContent.value = content || ''
    articleModel.value.content = editorContent.value
}

const isRichTextEmpty = (html) => {
    if (!html) {
        return true
    }
    const text = html
        .replace(/<[^>]*>/g, '')
        .replace(/&nbsp;/gi, ' ')
        .trim()
    return text.length === 0
}

const validateArticleModel = () => {
    if (!articleModel.value.title || !articleModel.value.title.trim()) {
        ElMessage.warning('请输入文章标题')
        return false
    }
    if (!articleModel.value.categoryId) {
        ElMessage.warning('请选择文章分类')
        return false
    }
    if (!articleModel.value.coverImg) {
        ElMessage.warning('请上传文章封面')
        return false
    }
    if (isRichTextEmpty(articleModel.value.content)) {
        ElMessage.warning('请输入文章内容')
        return false
    }
    return true
}

//添加文章
const addArticle=async (clickState)=>{
    if (!validateArticleModel()) {
        return
    }
    //把发布状态赋值给数据模型
    articleModel.value.state=clickState;
    //调用接口
    let result=await articleAddService(articleModel.value);

    ElMessage.success(result.message || result.msg || '添加成功');

    //让抽屉消失
    visibleDrawer.value=false;

    //刷新当前列表
    articleList()
}

//展示编辑文章
const showEditDrawer = async (row) => {
    drawerTitle.value = '编辑文章'
    visibleDrawer.value = true

    try {
        const result = await articleDetailService(row.id)
        articleModel.value = {
            ...result.data
        }
        editorContent.value = result.data.content || ''
        assistantSuggestions.value = null
    } catch (e) {
        // 兜底使用列表行数据，避免抽屉打开后完全不可编辑
        articleModel.value = {
            id: row.id,
            title: row.title || '',
            categoryId: row.categoryId || '',
            summary: row.summary || '',
            coverImg: row.coverImg || '',
            content: row.content || '',
                    state: row.state || '',
            version: row.version
        }
        editorContent.value = articleModel.value.content
        assistantSuggestions.value = null
        ElMessage.warning('文章详情加载失败，已使用列表数据回填')
    }
}

const generatingSummary = ref(false)
const generateSummary = async () => {
    if (isRichTextEmpty(articleModel.value.content)) {
        ElMessage.warning('请先输入文章内容')
        return
    }

    generatingSummary.value = true
    try {
        const result = await articleSummaryService(articleModel.value.content)
        articleModel.value.summary = result.data
        ElMessage.success('摘要生成成功，请确认后保存')
    } finally {
        generatingSummary.value = false
    }
}

const generatingAssistant = ref(false)
const assistantSuggestions = ref(null)
const generateAssistantSuggestions = async () => {
    if (isRichTextEmpty(articleModel.value.content)) {
        ElMessage.warning('请先输入文章内容')
        return
    }

    generatingAssistant.value = true
    try {
        const result = await articleAssistantService(articleModel.value.id, articleModel.value.content)
        assistantSuggestions.value = result.data
        ElMessage.success('AI 创作建议生成成功，请按需采纳')
    } finally {
        generatingAssistant.value = false
    }
}

const applySummarySuggestion = () => {
    articleModel.value.summary = assistantSuggestions.value.summary
    ElMessage.success('已采纳 AI 摘要')
}

const applyTitleSuggestion = (title) => {
    articleModel.value.title = title
    ElMessage.success('已采纳标题建议')
}

//更新文章
const updateArticle = async (clickState) => {
    if (!validateArticleModel()) {
        return
    }
    articleModel.value.state = clickState

    const result = await articleUpdateService(articleModel.value)
    ElMessage.success(result.message || result.msg || '更新成功')

    visibleDrawer.value = false
    articleList()
}

//删除文章
const deleteArticle = (row) => {
    ElMessageBox.confirm(
        '你确认要删除该文章吗?',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning'
        }
    )
        .then(async () => {
            const result = await articleDeleteService(row.id)
            ElMessage.success(result.message || result.msg || '删除成功')

            if (articles.value.length === 1 && pageNum.value > 1) {
                pageNum.value--
            }
            articleList()
        })
        .catch((e) => {
            if (e === 'cancel' || e === 'close') {
                ElMessage.info('用户取消了删除')
                return
            }
            ElMessage.error('删除失败')
        })
}
</script>
<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>文章管理</span>
                <div class="extra">
                    <el-button type="primary" @click="showAddDrawer">添加文章</el-button>
                </div>
            </div>
        </template>
        <!-- 搜索表单 -->
        <el-form inline class="search-form">
            <el-form-item label="文章分类：">
                <el-select class="filter-select" placeholder="请选择" v-model="categoryId" clearable>
                    <el-option 
                        v-for="c in categorys" 
                        :key="c.id" 
                        :label="c.categoryName"
                        :value="c.id">
                    </el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="发布状态：">
                <el-select class="filter-select" placeholder="请选择" v-model="state" clearable>
                    <el-option v-for="item in stateOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="articleList">搜索</el-button>
                <el-button @click="resetSearch">重置</el-button>
            </el-form-item>
        </el-form>
        <!-- 文章列表 -->
        <el-table :data="articles" style="width: 100%">
            <el-table-column label="文章标题" width="400" prop="title"></el-table-column>
            <el-table-column label="分类" prop="categoryName"></el-table-column>
            <el-table-column label="发表时间" prop="createTime"> </el-table-column>
            <el-table-column label="状态" prop="state"></el-table-column>
            <el-table-column label="操作" width="100">
                <template #default="{ row }">
                    <el-button :icon="Edit" circle plain type="primary" @click="showEditDrawer(row)"></el-button>
                    <el-button :icon="Delete" circle plain type="danger" @click="deleteArticle(row)"></el-button>
                </template>
            </el-table-column>
            <template #empty>
                <el-empty description="没有数据" />
            </template>
        </el-table>
        <!-- 分页条 -->
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[3, 5 ,10, 15]"
            layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
            @current-change="onCurrentChange" style="margin-top: 20px; justify-content: flex-end" />

            <!-- 抽屉 -->
        <el-drawer v-model="visibleDrawer" :title="drawerTitle" direction="rtl" size="50%">
            <!-- 添加文章表单 -->
            <el-form :model="articleModel" label-width="100px" >
                <el-form-item label="文章标题" >
                    <el-input v-model="articleModel.title" placeholder="请输入标题"></el-input>
                </el-form-item>
                <el-form-item label="文章分类">
                    <el-select placeholder="请选择" v-model="articleModel.categoryId">
                        <el-option v-for="c in categorys" :key="c.id" :label="c.categoryName" :value="c.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="文章封面">

                    <el-upload class="avatar-uploader" :auto-upload="true" :show-file-list="false"
                    action="/api/upload"
                    name="file"
                    :headers="{'Authorization':tokenStore.token}"
                    :before-upload="beforeCoverUpload"
                    :on-success="uploadSuccess"
                    :on-progress="coverUploadProgress"
                    :on-error="coverUploadError"
                    >
                        <img v-if="articleModel.coverImg" :src="articleModel.coverImg" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                    <el-progress v-if="uploadingCover" :percentage="uploadProgress" :stroke-width="6" />
                </el-form-item>
                <el-form-item label="文章摘要">
                    <div class="summary-field">
                        <el-input
                            v-model="articleModel.summary"
                            type="textarea"
                            :rows="3"
                            maxlength="500"
                            show-word-limit
                            placeholder="可手动填写，也可以根据正文生成"
                        />
                        <el-button
                            type="primary"
                            plain
                            :loading="generatingSummary"
                            @click="generateSummary"
                        >
                            AI 生成摘要
                        </el-button>
                    </div>
                </el-form-item>
                <el-form-item label="文章内容">
                    <div class="editor"><quill-editor
                        :key="editorKey"
                        theme="snow"
                        v-model:content="editorContent"
                        content-type="html"
                        @update:content="syncEditorContent"
                        >
            </quill-editor>
            </div>
                </el-form-item>
                <el-form-item label="AI 创作助手">
                    <div class="assistant-field">
                        <div class="assistant-actions">
                            <span>根据正文生成标题、摘要和标签建议</span>
                            <el-button type="success" plain :loading="generatingAssistant" @click="generateAssistantSuggestions">生成创作建议</el-button>
                        </div>
                        <div v-if="assistantSuggestions" class="assistant-result">
                            <div class="suggestion-section">
                                <div class="suggestion-heading"><strong>摘要建议</strong><el-button link type="primary" @click="applySummarySuggestion">采纳摘要</el-button></div>
                                <p>{{ assistantSuggestions.summary }}</p>
                            </div>
                            <div class="suggestion-section">
                                <strong>标题候选</strong>
                                <div class="title-suggestions">
                                    <el-button v-for="title in assistantSuggestions.titleSuggestions" :key="title" plain @click="applyTitleSuggestion(title)">{{ title }}</el-button>
                                </div>
                            </div>
                            <div class="suggestion-section">
                                <strong>标签建议</strong>
                                <div class="tag-suggestions"><el-tag v-for="tag in assistantSuggestions.tags" :key="tag" effect="plain">{{ tag }}</el-tag></div>
                            </div>
                        </div>
                    </div>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="drawerTitle === '添加文章' ? addArticle('已发布') : updateArticle('已发布')">发布</el-button>
                    <el-button type="info" @click="drawerTitle === '添加文章' ? addArticle('草稿') : updateArticle('草稿')">草稿</el-button>
                </el-form-item>
            </el-form>
        </el-drawer>

    </el-card>
</template>
<style lang="scss" scoped>
.page-container {
    min-height: 100%;
    box-sizing: border-box;

    .header {
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
}

.search-form {
    :deep(.filter-select) {
        width: 180px;
    }
}

.avatar-uploader {
    :deep() {
        .avatar {
            width: 178px;
            height: 178px;
            display: block;
        }

        .el-upload {
            border: 1px dashed var(--el-border-color);
            border-radius: 6px;
            cursor: pointer;
            position: relative;
            overflow: hidden;
            transition: var(--el-transition-duration-fast);
        }

        .el-upload:hover {
            border-color: var(--el-color-primary);
        }

        .el-icon.avatar-uploader-icon {
            font-size: 28px;
            color: #8c939d;
            width: 178px;
            height: 178px;
            text-align: center;
        }
    }
}
.editor {
  width: 100%;
  :deep(.ql-editor) {
    min-height: 200px;
  }
}
.summary-field, .assistant-field { width: 100%; }
.summary-field { display: flex; gap: 12px; align-items: flex-start; }
.assistant-field { border: 1px solid #d9e8de; border-radius: 6px; overflow: hidden; }
.assistant-actions { display: flex; align-items: center; justify-content: space-between; gap: 12px; padding: 12px 14px; background: #f4faf6; color: #52715d; font-size: 13px; }
.assistant-result { padding: 14px; background: #fff; }
.suggestion-section + .suggestion-section { margin-top: 14px; }
.suggestion-heading { display: flex; align-items: center; justify-content: space-between; }.suggestion-section p { margin: 7px 0 0; color: #526258; line-height: 1.7; white-space: pre-wrap; }
.title-suggestions, .tag-suggestions { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 8px; }
@media (max-width: 640px) { .summary-field { flex-direction: column; }.assistant-actions { align-items: flex-start; flex-direction: column; } }
</style>