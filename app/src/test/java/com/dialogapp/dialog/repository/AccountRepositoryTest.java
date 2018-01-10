package com.dialogapp.dialog.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.dialogapp.dialog.api.MicroblogService;
import com.dialogapp.dialog.util.InstantAppExecutors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class AccountRepositoryTest {
    private MicroblogService microblogService;
    private AccountRepository accountRepository;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        microblogService = mock(MicroblogService.class);
        accountRepository = new AccountRepository(new InstantAppExecutors(), microblogService);
    }
}
