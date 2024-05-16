package com.camus.backend.filter.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
public class AhoCorasick {
	private Node root;

	public AhoCorasick(String[] keywords) {
		root = new Node();
		// 트라이 구축
		for (String word : keywords) {
			Node current = root;
			for (char ch : word.toCharArray()) {
				current = current.children.computeIfAbsent(ch, c -> new Node());
			}
			current.end = true; // 단어의 끝을 표시
		}
		// 실패 링크 구축
		buildFailureLinks();
	}

	private void buildFailureLinks() {
		Queue<Node> queue = new LinkedList<>();
		for (Node child : root.children.values()) {
			child.fail = root;
			queue.add(child);
		}

		while (!queue.isEmpty()) {
			Node current = queue.remove();

			for (Map.Entry<Character, Node> entry : current.children.entrySet()) {
				Node child = entry.getValue();
				char ch = entry.getKey();

				Node fail = current.fail;
				while (fail != null && !fail.children.containsKey(ch)) {
					fail = fail.fail;
				}
				child.fail = (fail != null) ? fail.children.get(ch) : root;

				if (child.fail != null && child.fail.end) {
					child.end = true;
				}

				queue.add(child);
			}
		}
	}

	public boolean containsAny(String text) {
		Node current = root;
		for (char ch : text.toCharArray()) {
			while (current != null && !current.children.containsKey(ch)) {
				current = current.fail;
			}
			if (current == null) {
				current = root;
				continue;
			}
			current = current.children.get(ch);
			if (current.end) {
				return true; // 패턴 발견
			}
		}
		return false;
	}

	private static class Node {
		Map<Character, Node> children = new HashMap<>();
		Node fail;
		boolean end = false;
	}
}

