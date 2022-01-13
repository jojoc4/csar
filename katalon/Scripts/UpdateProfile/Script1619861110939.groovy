import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

WebUI.click(findTestObject('Object Repository/Page_Home - a sappelle retour/a_Mon compte'))

WebUI.setText(findTestObject('Object Repository/Page_Home - a sappelle retour/input_Nom_lastname'), 'Test2')

WebUI.doubleClick(findTestObject('Object Repository/Page_Home - a sappelle retour/input_Prnom_firstname'))

WebUI.setText(findTestObject('Object Repository/Page_Home - a sappelle retour/input_Prnom_firstname'), 'Yo')

WebUI.setEncryptedText(findTestObject('Object Repository/Page_Home - a sappelle retour/input_Ancien mot de passe_passwordold'), 
    '/5S6MFFLcE4dwTDJ57+urg==')

WebUI.setEncryptedText(findTestObject('Object Repository/Page_Home - a sappelle retour/input_Nouveau mot de passe_passwordnew'), 
    '/5S6MFFLcE47TFgtSVsT0Q==')

WebUI.click(findTestObject('Object Repository/Page_Home - a sappelle retour/button_Enregistrer'))

