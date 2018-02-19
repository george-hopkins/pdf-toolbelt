/*
 * Copyright 2018 George Hopkins <george-hopkins@null.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.georgehopkins.pdftoolbelt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

public class Application {
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			throw new IllegalArgumentException("Usage: [form-font-defaults] [...]");
		}
		switch (args[0]) {
		case "form-font-defaults":
			if (args.length != 3) {
				throw new IllegalArgumentException("Usage: form-font-defaults input.pdf output.pdf");
			}
			formFontDefaults(new FileInputStream(args[1]), new FileOutputStream(args[2]));
		}
	}

	static void formFontDefaults(InputStream input, OutputStream output) throws IOException {
		PDDocument document = PDDocument.load(input);
		PDAcroForm form = document.getDocumentCatalog().getAcroForm();
		if (form == null) {
			throw new IllegalArgumentException("Not form found.");
		}

		PDResources formResources = form.getDefaultResources();
		COSName fontName = COSName.getPDFName("Helv");
		PDFont font = form.getDefaultResources().getFont(fontName);
		if (font == null) {
			font = PDType1Font.HELVETICA;
			formResources.put(fontName, font);
		}

		String appearance = String.format("/%s 0 Tf 0 g", fontName.getName());
		form.setDefaultAppearance(appearance);

		Iterator<PDField> fields = form.getFieldIterator();
		while (fields.hasNext()) {
			PDField field = fields.next();
			if (field instanceof PDTextField) {
				PDTextField textField = (PDTextField) field;
				textField.setDefaultAppearance(appearance);
			}
		}

		document.save(output);
	}
}
