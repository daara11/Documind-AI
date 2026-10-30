DocuMind AI – AI-Powered PDF Question Answering System

DocuMind AI is an AI-powered web application that allows users to upload PDF documents, extract their content, generate intelligent summaries, and ask questions about the uploaded documents.
The application combines Java, Spring Boot, Apache PDFBox, and a locally running Ollama language model to provide an interactive document analysis experience.

Features:
- PDF Upload: Upload and process PDF documents.
- Text Extraction: Extract text from PDF files using Apache PDFBox.
- AI Summarization: Generate summaries of lengthy PDF documents using Ollama.
- Document Question Answering: Ask questions and receive answers based on document content.
- General AI Chat: Interact with the AI assistant beyond document-related questions.
- Chunk-Based Processing: Process lengthy documents in smaller sections.
- Web Interface: Simple and interactive user interface.

Technologies Used:
Technology| Purpose
Java| Backend development
Spring Boot| Web application framework
Apache PDFBox| PDF text extraction
Ollama| Local AI model integration
Llama 3.2 (3B)| Language model
HTML, CSS, JavaScript| Frontend development
Maven| Dependency management

Prerequisites:
- Java JDK
- Maven (or the included Maven Wrapper)
- Ollama

Running the Project:

1. Clone the repository:
git clone https://github.com/daara11/Documind-AI.git

2. Open the project folder.

3. Install and start Ollama.

4. Download the model:
ollama pull llama3.2:3b

5. Run the Spring Boot application from the project directory:
mvnw.cmd spring-boot:run

6. Open the application in your browser:
http://localhost:8080/index.html

Future Enhancements:

- Support for additional document formats.
- Improved AI response speed.
- Enhanced document search and retrieval.
- Better user experience and interface improvements.

Author:

Venu Veenadaara S

B.Tech – Information Technology

GitHub: https://github.com/daara11


Built as a learning,beginner and portfolio project using Java, Spring Boot, and Local AI.


## 📸 Application Preview

### Homepage
![DocuMind AI Homepage](Screenshot%202026-10-30%20145208.png)
