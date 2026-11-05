#include <jni.h>
#include <string>
#include <zim/zim.h>
#include <zim/archive.h>
#include <zim/writer/creator.h>
#include <zim/writer/item.h>
#include <zim/writer/contentProvider.h>
#include <zim/search.h>
#include <zim/item.h>
#include <memory>
#include <android/log.h>

#define LOG_TAG "ZimEngine"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

class TextItem : public zim::writer::Item {
    std::string path;
    std::string title;
    std::string content;
public:
    TextItem(const std::string& p, const std::string& t, const std::string& c) 
        : path(p), title(t), content(c) {}
    std::string getPath() const override { return path; }
    std::string getTitle() const override { return title; }
    std::string getMimeType() const override { return "text/plain"; }
    std::unique_ptr<zim::writer::ContentProvider> getContentProvider() const override {
        return std::make_unique<zim::writer::StringProvider>(content);
    }
};

extern "C" JNIEXPORT jstring JNICALL
Java_com_jegly_offlineLLM_zim_ZimEngine_getZimVersion(JNIEnv *env, jobject /* this */) {
    // Placeholder: actual libzim version API will be implemented in Task 1.4
    // This verifies that the real libzim header is included and linked
    std::string version = "libzim-integrated";
    return env->NewStringUTF(version.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_jegly_offlineLLM_zim_ZimEngine_getZimMetadata(JNIEnv *env, jobject /* this */, jstring path) {
    const char *nativePath = env->GetStringUTFChars(path, nullptr);
    std::string result;

    try {
        zim::Archive archive(nativePath);
        std::string title = archive.getMetadata("Title");
        zim::entry_index_type count = archive.getEntryCount();
        
        result = "Title: " + title + " | Entries: " + std::to_string(count);
    } catch (const std::exception &e) {
        LOGE("Failed to open ZIM: %s", e.what());
        result = "Error: " + std::string(e.what());
    }

    env->ReleaseStringUTFChars(path, nativePath);
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_jegly_offlineLLM_zim_ZimEngine_createZim(JNIEnv *env, jobject /* this */, jstring jPath, jstring jTitle, jstring jContent) {
    const char* path = env->GetStringUTFChars(jPath, nullptr);
    const char* title = env->GetStringUTFChars(jTitle, nullptr);
    const char* content = env->GetStringUTFChars(jContent, nullptr);
    
    bool success = false;
    try {
        zim::writer::Creator creator;
        creator.configVerbose(false);
        creator.startZimCreation(path);
        
        auto item = std::make_shared<TextItem>("main", title, content);
        creator.addItem(item);
        
        creator.finishZimCreation();
        success = true;
    } catch (const std::exception& e) {
        LOGE("ZIM Creation failed: %s", e.what());
    }

    env->ReleaseStringUTFChars(jPath, path);
    env->ReleaseStringUTFChars(jTitle, title);
    env->ReleaseStringUTFChars(jContent, content);
    return success;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_jegly_offlineLLM_zim_ZimEngine_searchZim(JNIEnv *env, jobject /* this */, jstring jPath, jstring jQuery) {
    const char* path = env->GetStringUTFChars(jPath, nullptr);
    const char* queryStr = env->GetStringUTFChars(jQuery, nullptr);
    std::string result = "";

    try {
        zim::Archive archive(path);
        zim::Searcher searcher(archive);
        zim::Query query;
        query.setQuery(queryStr);
        
        auto search = searcher.search(query);
        auto results = search.getResults(0, 5); // Get top 5 results
        
        for (auto it = results.begin(); it != results.end(); ++it) {
            result += it->getTitle() + " | " + it->getPath() + "\n";
        }
        
        if (result.empty()) {
            result = "No results found.";
        }
    } catch (const std::bad_alloc& e) {
        LOGE("ZIM Out of Memory: %s", e.what());
        result = "Error: Out of memory while processing ZIM file.";
    } catch (const std::exception& e) {
        LOGE("ZIM Search failed: %s", e.what());
        result = "Error: " + std::string(e.what());
    }

    env->ReleaseStringUTFChars(jPath, path);
    env->ReleaseStringUTFChars(jQuery, queryStr);
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_jegly_offlineLLM_zim_ZimEngine_readZimEntry(JNIEnv *env, jobject /* this */, jstring jPath, jstring jEntryPath) {
    const char* path = env->GetStringUTFChars(jPath, nullptr);
    const char* entryPath = env->GetStringUTFChars(jEntryPath, nullptr);
    std::string result = "";
    try {
        zim::Archive archive(path);
        zim::Entry entry = archive.getEntryByPath(entryPath);
        zim::Item item = entry.getItem(true);
        result = std::string(item.getData(), item.getSize());
    } catch (const std::bad_alloc& e) {
        LOGE("ZIM Out of Memory: %s", e.what());
        result = "Error: Out of memory while processing ZIM file.";
    } catch (const std::exception& e) {
        LOGE("ZIM Read failed: %s", e.what());
        result = "Error: " + std::string(e.what());
    }
    env->ReleaseStringUTFChars(jPath, path);
    env->ReleaseStringUTFChars(jEntryPath, entryPath);
    return env->NewStringUTF(result.c_str());
}
