## Тестовое задание для стажера на позицию «Автотестирование (Java)

### Задание 1
Реализован клиент для работы с FTP сервером, на котором расположен файл с информацией о студентах в виде JSON подобной структуры:
```json
{
   "students": [
      {
         "id": 1,
         "name": "Student1"
      },
      {
         "id": 2,
         "name": "Student2"
      },
      {
         "id": 3,
         "name": "Student3"
      }
   ]
}
```

Клиент разработан в виде консольного приложения, принимающего на вход логин, пароль пользователя и IP-адрес FTP-сервера. После подключение приложение принимает на вход имя файла, расположенного на сервере, и выводит меню, в котором доступны на выбор следующие действия:
1.	Получение списка студентов по имени
2.	Получение информации о студенте по id
3.	Добавление студента (id генерируется автоматически)
4.	Удаление студента по id
5.	Завершение работы
6.  Получение списка всех команд
7.  Получение отсортированного по алфавиту списка всех студентов

Целевая платформа: **Linux**

В качестве FTP-сервера используется готовое решение.

* Тестовое задание выполнено с использованием **Java SE 8**.
* Клиент умеет работать с сервером в двух режимах: активном и пассивном.
* Список студентов при выводе отсортирован по алфавиту
* id студента уникален

По умолчанию клиент работает в пассивном режиме. Использовать активный режим можно двумя способами:
1. Задать значение **false** переменной **passiveMode** в классе **ConsoleClientApplication**
2. Передать значение **false** при вызове
```java
public String getDataFromFile(String remoteFilePath, boolean passiveMode);
public boolean saveDataToFile(String data, String remoteFilePath, boolean passiveMode);
```
находящихся в класcе **FTPClient**. Адрес клиента в активном режиме по умолчанию задан как **127.0.0.1**, но его можно изменить в том же классе:
```java
 private final String clientIPActiveMode = "127.0.0.1";  
```  
  
  
### Инструкция по сборке и запуску проекта: 

#### Клонирование репозитория
Выполните следующую команду, чтобы клонировать репозиторий с GitHub:
`git clone https://github.com/petcheetos/console-ftp-client.git`

#### Переход в директорию, куда был клонирован репозиторий
`cd console-ftp-client`

#### Компиляция проекта 
Создайте директорию для скомпилированных классов
`mkdir -p out`

Скомпилируйте все классы
`javac -d out src/main/java/edu/java/Main.java src/main/java/edu/java/ConsoleClientApplication.java src/main/java/edu/java/commands/*.java src/main/java/edu/java/console/*.java src/main/java/edu/java/ftp/*.java src/main/java/edu/java/utils/*.java src/main/java/edu/java/entities/*.java src/main/java/edu/java/services/*.java`
или
`javac -d out -sourcepath src/main/java src/main/java/edu/java/Main.java`

#### Создание JAR файла
Перейдите в директорию с скомпилированными классами
`cd out`

Объедините классы в исполняемый jar-файл:
`jar cvfe ConsoleClientApplication.jar Main *.class */*.class`
`jar cvfe ../ConsoleClientApplication.jar edu.java.Main edu/java/*.class edu/java/commands/*.class edu/java/console/*.class edu/java/ftp/*.class edu/java/utils/*.class edu/java/entities/*.class edu/java/services/*.class`

#### Запуск приложения
`java -jar ConsoleClientApplication.jar`



