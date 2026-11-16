import { expect, test } from '@playwright/test';

test('la pantalla de acceso de iDempiere está disponible', async ({ page }) => {
  const response = await page.goto('/webui/index.zul', { waitUntil: 'load' });

  expect(response?.status()).toBe(200);
  await expect(page.locator('input[type="password"]').first()).toBeVisible();
});
