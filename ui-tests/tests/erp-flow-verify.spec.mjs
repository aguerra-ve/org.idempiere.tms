import { expect, test } from '@playwright/test';

test('verifica la entrega final completada en el clon', async ({ page }, testInfo) => {
  test.skip(process.env.IDEMPIERE_TEST_ERP_FLOW !== '1', 'Requiere el caso ERP IQP-MAP-01 del clon');
  const user = process.env.IDEMPIERE_TEST_USER;
  const password = process.env.IDEMPIERE_TEST_PASSWORD;
  test.skip(!user || !password, 'Requiere una cuenta autorizada');

  await page.goto('/webui/index.zul');
  await page.locator('input[type="password"]').first().waitFor({ state: 'visible' });
  await page.locator('input[type="text"]').first().fill(user);
  await page.locator('input[type="password"]').first().fill(password);
  await page.getByRole('button', { name: 'OK' }).click();
  await page.locator('.z-combobox-input').first().waitFor({ state: 'visible' });
  await page.getByText('Select Role', { exact: true }).waitFor({ state: 'visible' });
  await page.locator('.z-combobox-input').nth(1).locator('..').locator('.z-combobox-button').click();
  await page.getByText('GardenWorld Admin', { exact: true }).click();
  await page.getByRole('button', { name: 'OK' }).last().click();

  const search = page.getByPlaceholder('Alt+G');
  await search.waitFor({ state: 'visible' });
  await search.fill('Shipment');
  const result = page.locator('.z-bandbox-popup').getByText('Shipment (Customer)', { exact: true }).first();
  await expect(result).toBeVisible();
  await page.waitForTimeout(700);
  await result.click();

  await expect(page.locator('[instancename="M_InOut0DocumentNo"]')).toHaveValue('500001');
  await expect(page.locator('[instancename="M_InOut0DocStatus"] input')).toHaveValue('Completed');
  await expect(page.locator('[instancename="M_InOut0C_Order_ID"] input')).toHaveValue(/IQP-MAP-01/);
  await expect(page.locator('.z-row').filter({ hasText: 'Plum Tree' }).last()).toContainText('IQP-MAP-01');
  await page.locator('[instancename="M_InOut0DocStatus"]').scrollIntoViewIfNeeded();
  await page.screenshot({ path: testInfo.outputPath('final-shipment.png'), fullPage: true });
  await page.locator('a[title="Next record    Alt+Right"]').click();
  await expect(page.locator('[instancename="M_InOut0DocumentNo"]')).toHaveValue('500000');
  await expect(page.locator('[instancename="M_InOut0DocStatus"] input')).toHaveValue('Completed');
  await expect(page.locator('[instancename="M_InOut0C_Order_ID"] input')).toHaveValue(/IQP-MAP-01/);
  await page.locator('[instancename="M_InOut0DocStatus"]').scrollIntoViewIfNeeded();
  await page.screenshot({ path: testInfo.outputPath('partial-shipment.png'), fullPage: true });
});
