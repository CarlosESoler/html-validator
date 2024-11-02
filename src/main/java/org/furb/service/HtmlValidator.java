package org.furb.service;

import org.furb.dataStructure.Stack;
import org.furb.dataStructure.StaticList;
import org.furb.dataStructure.sort.AbstractSort;
import org.furb.dataStructure.sort.OptimizedBubbleSort;
import org.furb.exception.IllegalTagsSequence;
import org.furb.exception.InvalidHtmlStructureException;
import org.furb.exception.InvalidTagException;
import org.furb.model.Tag;


public class HtmlValidator {
    private final Stack<Tag> tagStack;
    public final StaticList<Tag> tagList;

    public HtmlValidator() {
        tagStack = new Stack<>();
        tagList = new StaticList<>();
    }

    public boolean isValidTags(String[] tagsContent) {
        for (String content : tagsContent) {
            Tag tag = new Tag(content);
            if (!tag.isValidTag()) throw new InvalidTagException("A tag " + content + " é inválida! Sua estrutura não segue os requisitos.");

            tag.tagClear();

            if (tag.isSingleton()){
                this.putFrequency(tag);
            } else {
                updateTagStack(tag);
            }
        }
        return true;
    }

    private void updateTagStack(Tag tag) {
        if (!tag.isFinal()) {
            tagStack.push(tag);
            this.putFrequency(tag);
        } else {
            if (tagStack.isEmpty()){
                throw new InvalidHtmlStructureException("Formato do arquivo inválido! Foi encontrada a tag " + tag.getContent() + " quando não há tag para fechar.");
            }

            Tag lastTag = tagStack.pop();
            String expectedTag = lastTag.getContentCleared().replace("<", "</");
            if (!expectedTag.equalsIgnoreCase(tag.getContentCleared())){
                throw new IllegalTagsSequence("Formato do arquivo inválido!\nTag encontrada: " + tag.getContent() + "\nTag esperada: " + expectedTag);
            }
        }
    }


    private void putFrequency(Tag tag) {
        int position = tagList.search(tag);

        if (position < 0) {
            tag.setFrequency(1);
            tagList.insert(tag);
            return;
        }
        Tag tagInList = tagList.get(position);
        tagInList.setFrequency(tagInList.getFrequency() + 1);
    }

    private Tag[] castListToArray() {
        Tag[] tagsFromObjectList = new Tag[tagList.getSize()];
        for (int i = 0; i < tagList.getSize(); i++) {
            tagsFromObjectList[i] = tagList.get(i);
        }
        return tagsFromObjectList;
    }

    public Tag[] orderByNameDesc() {
        Tag[] castedTagList = this.castListToArray();
        AbstractSort<Tag> optimizedBubbleSort = new OptimizedBubbleSort<>(castedTagList);
        optimizedBubbleSort.sort();
        return castedTagList;
    }

    public boolean checkStackIsEmpty() {
        if (!tagStack.isEmpty()) {
            Tag current = tagStack.pop();
            StringBuilder message = new StringBuilder("Formato do arquivo inválido! Faltaram as seguintes tags, nesta ordem:");

            while (current != null) {
                message.append("\n")
                        .append(current.getContentCleared().replace("<", "</"));
                try {
                    current = tagStack.pop();
                } catch (Exception e) {
                    current = null;
                }
            }
            throw new IllegalTagsSequence(message.toString());
        }
        return true;
    }
}
